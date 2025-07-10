package com.hearthappy.router.compiler

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.hearthappy.router.analysis.TargetObject
import com.hearthappy.router.analysis.TargetServiceProvider
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.constant.Constant
import com.hearthappy.router.datahandler.DataInspector
import com.hearthappy.router.datahandler.DataInspector.EMPTY_STRING
import com.hearthappy.router.datahandler.reRouterName
import com.hearthappy.router.enums.RouteType
import com.hearthappy.router.ext.AndroidTypeNames
import com.hearthappy.router.ext.KotlinTypeNames
import com.hearthappy.router.ext.RouterTypeNames
import com.hearthappy.router.ext.RouterTypeNames.AutowiredService
import com.hearthappy.router.ext.RouterTypeNames.PathReplaceService
import com.hearthappy.router.ext.RouterTypeNames.ProviderService
import com.hearthappy.router.ext.RouterTypeNames.SerializationService
import com.hearthappy.router.generater.IPoetFactory
import com.hearthappy.router.generater.impl.PoetFactory
import com.hearthappy.router.logger.KSPLog
import com.hearthappy.router.model.RouterInfo
import com.hearthappy.router.visitor.RouterVisitor
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

/**
 * Created Date: 2025/6/7
 * @author ChenRui
 * ClassDescription： Parse and generate routing files
 */
class RouterProcessor(private val codeGenerator: CodeGenerator, private val poetFactory: IPoetFactory) : SymbolProcessor { // 使用线程安全的集合存储路由信息
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val routeSymbols = resolver.getSymbolsWithAnnotation(Route::class.qualifiedName!!)
        val invalidSymbols = routeSymbols.filter { it.validate() }.toList()
        if (invalidSymbols.isEmpty()) return emptyList()

        val declarationSequence = routeSymbols.filterIsInstance<KSClassDeclaration>()
        val took = measureTimeMillis {
            runBlocking {
                val routeChannel = Channel<RouterInfo>(Channel.UNLIMITED)

                val producer = producerJob(routeChannel, declarationSequence)

                val consumer = consumerJob(routeChannel, declarationSequence.count())

                joinAll(producer, consumer)
            }
        }
        KSPLog.printRouterTook(declarationSequence.count(), took)
        return emptyList()
    }

    /**
     * 消费者协程：路由文件的生成
     * @receiver CoroutineScope
     * @param routeChannel Channel<RouterInfo>
     * @param totalCount Int
     * @return Job
     */
    private fun CoroutineScope.consumerJob(routeChannel: Channel<RouterInfo>, totalCount: Int): Job {
        return launch(Dispatchers.IO) {
            var count = 0
            routeChannel.consumeEach {
                generateRouterTable(it)
                if (++count == totalCount) routeChannel.close()
            }
        }
    }

    /**
     * 生产者协程：符号解析
     * @receiver CoroutineScope
     * @param routeChannel Channel<RouterInfo>
     * @param declarationSequence Sequence<KSClassDeclaration>
     * @return Job
     */
    private fun CoroutineScope.producerJob(routeChannel: Channel<RouterInfo>, declarationSequence: Sequence<KSClassDeclaration>): Job {
        return launch(Dispatchers.Default) {
            declarationSequence.forEach { it.accept(RouterVisitor(routeChannel, this), Unit) }
        }
    }


    private fun generateRouterTable(info: RouterInfo) {
        poetFactory.apply {
            KSPLog.print("path = ${info.path} , class = ${info.clazz} ")
            when (info.routerMeta.routeType) {
                RouteType.ACTIVITY, RouteType.FRAGMENT, RouteType.SERVICE -> {
                    generatePath(info)
                    generateRouter(info)
                }

                RouteType.SERVICE_PROVIDER -> {
                    generateProvider(info)
                }

                else -> Unit
            }
        }
    }

    private fun IPoetFactory.generateProvider(info: RouterInfo) {
        val providerFileName = info.routerMeta.superClass.run {
            when (this) {
                SerializationService -> "Provider$$".plus("Json")
                PathReplaceService -> "Provider$$".plus("PathReplace")
                ProviderService -> {
                    generatePath(info)
                    "Provider$$".plus(info.routerMeta.parent?.simpleName)
                }
                else -> EMPTY_STRING
            }
        }
        if (providerFileName.isEmpty()) return
        val classPkg = info.clazz.substringBeforeLast('.')
        val simpleName = info.clazz.substringAfterLast('.')
        val classSpec = createClassSpec(providerFileName).addAnnotation(AnnotationSpec.builder(TargetServiceProvider::class).addMember("%L::class", info.clazz.substringAfterLast('.')).build())
        createFileSpec(providerFileName, Constant.GENERATE_ROUTER_PROVIDER_PKG).addImport(classPkg, listOf(simpleName)).buildAndWrite(classSpec.build(), info.containingFile!!, codeGenerator)

    }


    private fun IPoetFactory.generateRouter(info: RouterInfo) {
        val routerFileName = "Router$$".plus(info.clazz.substringAfterLast('.'))
        val routerClassSpec = createClassSpec(routerFileName, superClassName = null, constructorParameters = emptyList(), isAddConstructorProperty = false)
        val createFileSpec = createFileSpec(routerFileName, Constant.GENERATE_ROUTER_ROUTES_PKG)
        routerClassSpec.addSuperinterface(AutowiredService).addFunction(FunSpec.builder("inject").addParameter(ParameterSpec.builder("thiz", KotlinTypeNames.Any).build()).apply {
            if (info.params.isNotEmpty()) {
                val clazz = ClassName.bestGuess(info.clazz)
                info.pkg.add(clazz)
                addStatement("(thiz as ${clazz.simpleName}).apply{")
                val extras = if (info.routerMeta.routeType == RouteType.FRAGMENT) "arguments" else "intent.extras"
                addStatement("%L%L?.apply{", Constant.INDENTATION, extras)
                info.params.forEach {
                    val type = it.type.toString()
                    if (!it.isSystemPkg && it.autowiredParent?.simpleName == "Any") {
                        val serializationService = "serializationService"
                        val errorMessage = "The '${it.name}' field in the '${it.type.simpleName}' class needs to implement SerializationService to support automatic object injection"
                        val throwMessage = "The 'withObject' field with the lateinit or Delegates modifier cannot be empty"
                        routerClassSpec.addSpecProperty(serializationService, SerializationService.copy(nullable = true), isDelegate = true, receiver = null, delegate = CodeBlock.of("lazy{ Router.getInstance(SerializationService::class.java)}"), modifiers = arrayOf(KModifier.PRIVATE))
                        addStatement("%L%L%L?.let {", Constant.INDENTATION, Constant.INDENTATION, serializationService)
                        addStatement("%L%L%L%L = it.fromJson(getString(\"%L\") ,%L::class.java)${if (it.isLateinit) "?:throw RuntimeException(%S)" else "%L"}", Constant.INDENTATION, Constant.INDENTATION, Constant.INDENTATION, it.name, it.name, it.type.simpleName, if (it.isLateinit) throwMessage else "")
                        addStatement("%L%L}?:Log.e(\"Router\",%S)", Constant.INDENTATION, Constant.INDENTATION, errorMessage)
                        info.pkg.add(AndroidTypeNames.Log)
                    } else if (!it.isSystemPkg && it.autowiredParent == ProviderService) {
                        addStatement("%L%L${it.name} = Router.getInstance(${it.type.simpleName}::class.java)", Constant.INDENTATION, Constant.INDENTATION)
                    } else {
                        if (type in DataInspector.primitiveTypes) {
                            addStatement("%L%L%L = %L(\"%L\",%L)", Constant.INDENTATION, Constant.INDENTATION, it.name, it.autowiredType, it.name, if (it.isLateinit) DataInspector.toDefaultValue(type) else it.name)
                        } else {
                            addStatement("%L%L${it.name} = ${it.autowiredType}(\"${it.name}\")", Constant.INDENTATION, Constant.INDENTATION)
                        }

                    }
                }
                addStatement("%L}", Constant.INDENTATION)
                addStatement("}")
            }
        }.addModifiers(KModifier.OVERRIDE).build())

        //import package
        if (info.pkg.isNotEmpty()) for (className in info.pkg) createFileSpec.addImport(className.packageName, className.simpleName)

        createFileSpec.buildAndWrite(routerClassSpec.build(), info.containingFile!!, codeGenerator)
    }

    private fun IPoetFactory.generatePath(info: RouterInfo) {
        val pathFileName = "Path$$".plus(info.path.reRouterName())
        val classPkg = info.clazz.substringBeforeLast('.')
        val simpleName = info.clazz.substringAfterLast('.')
        val pathClassSpec = createClassSpec(pathFileName, superClassName = null, constructorParameters = emptyList(), isAddConstructorProperty = false)
        pathClassSpec.addAnnotation(AnnotationSpec.builder(TargetObject::class).addMember("%L::class", simpleName).addMember("%L", info.routerMeta.routeType.typeName()).build())
        val fileSpec = createFileSpec(pathFileName, Constant.GENERATE_ROUTER_PATH_PKG)
        fileSpec.addImport(classPkg, listOf(simpleName))
        fileSpec.addImport(RouterTypeNames.RouteType.packageName, RouterTypeNames.RouteType.simpleName)
        fileSpec.buildAndWrite(pathClassSpec.build(), info.containingFile!!, codeGenerator)
    }
}

class RouterProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val enableLog = environment.options["enableRouterLog"]?.toBoolean() ?: false
        if (enableLog) KSPLog.init(environment.logger)
        return RouterProcessor(environment.codeGenerator, PoetFactory())
    }
}

