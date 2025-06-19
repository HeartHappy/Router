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
import com.hearthappy.router.datahandler.DataInspector.EMPTY_STRING
import com.hearthappy.router.datahandler.reRouterName
import com.hearthappy.router.ext.CollectionsTypeNames
import com.hearthappy.router.ext.RouterTypeNames
import com.hearthappy.router.ext.RouterTypeNames.PathReplaceService
import com.hearthappy.router.ext.RouterTypeNames.SerializationService
import com.hearthappy.router.generater.IPoetFactory
import com.hearthappy.router.generater.impl.PoetFactory
import com.hearthappy.router.logger.KSPLog
import com.hearthappy.router.model.RouterInfo
import com.hearthappy.router.visitor.RouterVisitor
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.system.measureTimeMillis

/**
 * Created Date: 2025/6/7
 * @author ChenRui
 * ClassDescription： Parse and generate routing files
 */
class RouterProcessor(private val codeGenerator: CodeGenerator, private val poetFactory: IPoetFactory) : SymbolProcessor {

    private val routes = mutableMapOf<String, RouterInfo>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val measureTimeMillis = measureTimeMillis {
            val routeSymbols = resolver.getSymbolsWithAnnotation(Route::class.qualifiedName!!)
            val invalidSymbols = routeSymbols.filter { it.validate() }.toList()
            if (invalidSymbols.isEmpty()) return emptyList()
            routeSymbols.filterIsInstance<KSClassDeclaration>().forEach { it.accept(RouterVisitor(routes), Unit) }
            generateRouterTable()
        }
        KSPLog.printRouterTook(routes.size, measureTimeMillis)
        routes.clear()
        return emptyList()
    }

    private fun generateRouterTable() {
        routes.forEach { (path, info) ->
            poetFactory.apply {
                KSPLog.print("path = $path , class = ${info.clazz},supper:${info.routerType}")
                when (info.routerType) {
                    Constant.ROUTER_TYPE_ACTIVITY, Constant.ROUTER_TYPE_FRAGMENT -> {
                        generatePath(path, info)
                        generateRouter(info)
                    }

                    Constant.ROUTER_TYPE_SERVICE_PROVIDER -> {
                        generateProvider(info)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun generateProvider(info: RouterInfo) {
        val providerFileName = info.superType?.run {
            when (this) {
                SerializationService -> "Provider$$".plus("Json")
                PathReplaceService -> "Provider$$".plus("PathReplace")
                else -> EMPTY_STRING
            }
        } ?: EMPTY_STRING
        if (providerFileName.isEmpty()) return
        val classPkg = info.clazz.substringBeforeLast('.')
        val simpleName = info.clazz.substringAfterLast('.')
        poetFactory.apply {
            val classSpec = createClassSpec(providerFileName).addAnnotation(AnnotationSpec.builder(TargetServiceProvider::class).addMember("%L::class", info.clazz.substringAfterLast('.')).build())
            createFileSpec(providerFileName, Constant.GENERATE_ROUTER_PROVIDER_PKG).addImport(classPkg, listOf(simpleName)).buildAndWrite(classSpec.build(), info.containingFile!!, codeGenerator)
        }

    }


    private fun IPoetFactory.generateRouter(info: RouterInfo) {
        val routerFileName = "Router$$".plus(info.clazz.substringAfterLast('.'))
        val routerClassSpec = createClassSpec(routerFileName, superClassName = null, constructorParameters = emptyList(), isAddConstructorProperty = false)
        routerClassSpec.addSpecProperty("params", CollectionsTypeNames.List.parameterizedBy(RouterTypeNames.RouterParamInfo), null, false, CodeBlock.builder().apply {
            if (info.params.isEmpty()) {
                add("listOf()")
            } else {
                add("listOf(")
                info.params.forEach { add("\nParamInfo(\n\tname = \"${it.name}\", \n\tfieldName = \"${it.fieldName}\", \n\ttype = \"${it.type}\"),\n") }
                add(")")
            }
        }.build())
        routerClassSpec.buildAndWrite(routerFileName, Constant.GENERATE_ROUTER_ROUTES_PKG, containingFile = info.containingFile!!, codeGenerator)
    }

    private fun IPoetFactory.generatePath(path: String, info: RouterInfo) {
        val pathFileName = "Path$$".plus(path.reRouterName())
        val classPkg = info.clazz.substringBeforeLast('.')
        val simpleName = info.clazz.substringAfterLast('.')
        val pathClassSpec = createClassSpec(pathFileName, superClassName = null, constructorParameters = emptyList(), isAddConstructorProperty = false)
        pathClassSpec.addAnnotation(AnnotationSpec.builder(TargetObject::class).addMember("%L::class", simpleName).addMember("%L", info.routerType).build())
        val fileSpec = createFileSpec(pathFileName, Constant.GENERATE_ROUTER_PATH_PKG)
        fileSpec.addImport(classPkg, listOf(simpleName))
        fileSpec.addImport(RouterTypeNames.RouteType.packageName, RouterTypeNames.RouteType.simpleName)
        fileSpec.buildAndWrite(pathClassSpec.build(), info.containingFile!!, codeGenerator)
    }


}

class RouterProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val enableLog = environment.options["enableRouterLog"]?.toBoolean() ?: false
        if(enableLog) KSPLog.init(environment.logger)
        return RouterProcessor(environment.codeGenerator, PoetFactory())
    }
}

