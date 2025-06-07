package com.hearthappy.router.compiler

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.annotations.TargetActivity
import com.hearthappy.router.constant.Constant
import com.hearthappy.router.datahandler.reRouterName
import com.hearthappy.router.ext.CollectionsTypeNames
import com.hearthappy.router.ext.RouterTypeNames
import com.hearthappy.router.generater.IPoetFactory
import com.hearthappy.router.generater.impl.PoetFactory
import com.hearthappy.router.logger.KSPLog
import com.hearthappy.router.model.ParamInfo
import com.hearthappy.router.model.RouterInfo
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

/**
 * Created Date: 2025/6/7
 * @author ChenRui
 * ClassDescription： 解析并生成路由文件
 */
class RouterProcessor(private val codeGenerator: CodeGenerator, private val poetFactory: IPoetFactory) : SymbolProcessor {

    private val routes = mutableMapOf<String, RouterInfo>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val routeSymbols = resolver.getSymbolsWithAnnotation(Route::class.qualifiedName!!)

        val invalidSymbols = routeSymbols.filter { it.validate() }.toList()
        if (invalidSymbols.isEmpty()) return emptyList()
        routeSymbols.filterIsInstance<KSClassDeclaration>().forEach { it.accept(RouterVisitor(routes), Unit) }

        generateRouterTable()
        return emptyList()
    }

    private fun generateRouterTable() {
        routes.forEach { (path, info) ->
            poetFactory.apply {
                KSPLog.print("path = $path , class = ${info.clazz}")
                val pathFileName = "Path$$".plus(path.reRouterName())
                val pathClassSpec = createClassSpec(pathFileName, superClassName = null, constructorParameters = emptyList(), isAddConstructorProperty = false)
                pathClassSpec.addAnnotation(AnnotationSpec.builder(TargetActivity::class).addMember("name = \"${info.clazz}\"").build())
                pathClassSpec.buildAndWrite(pathFileName, Constant.GENERATE_ROUTER_PATH_PKG, containingFile = info.containingFile!!, codeGenerator)
                val routerFileName = "Router$$".plus(info.clazz.substringAfterLast('.'))
                val routerClassSpec = createClassSpec(routerFileName, superClassName = null, constructorParameters = emptyList(), isAddConstructorProperty = false)
                routerClassSpec.addSpecProperty("params", CollectionsTypeNames.List.parameterizedBy(RouterTypeNames.RouterParamInfo), null, false, CodeBlock.builder().apply {
                    add("listOf(")
                    info.params.forEach {
                        add("ParamInfo(name = \"${it.name}\", fieldName = \"${it.fieldName}\", type = \"${it.type}\"),\n")
                    }
                    add(")")
                }.build())
                routerClassSpec.buildAndWrite(routerFileName, Constant.GENERATE_ROUTER_ACT_PKG, containingFile = info.containingFile!!, codeGenerator)
            }
        }
    }



    class RouterVisitor(private val routes: MutableMap<String, RouterInfo>) : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            super.visitClassDeclaration(classDeclaration, data)
            processRouteClass(classDeclaration)
        }

        private fun processRouteClass(classDeclaration: KSClassDeclaration) {
            val routeAnnotation = classDeclaration.annotations.first { it.annotationType.resolve().declaration.qualifiedName?.asString() == Route::class.qualifiedName }

            val path = routeAnnotation.arguments.first { it.name?.asString() == "path" }.value as String

            val className = classDeclaration.qualifiedName?.asString() ?: return
            val routerInfo = RouterInfo()
            routerInfo.clazz = className
            routerInfo.containingFile = classDeclaration.containingFile

            classDeclaration.getAllProperties().forEach { property ->
                property.annotations.forEach { annotation ->
                    if (annotation.annotationType.resolve().declaration.qualifiedName?.asString() == Autowired::class.qualifiedName) {
                        val name = annotation.arguments.firstOrNull { it.name?.asString() == "name" }?.value as? String
                        val fieldName = property.simpleName.asString()
                        val paramName = name?.takeIf { it.isNotEmpty() }?.run { this } ?: fieldName
                        val paramInfo = ParamInfo(name = paramName, fieldName = fieldName, type = property.type.resolve().declaration.qualifiedName?.asString() ?: "")
                        routerInfo.params.add(paramInfo)
                    }
                }
            }
            routes[path] = routerInfo
        }
    }


}

class RouterProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        KSPLog.init(environment.logger)
        return RouterProcessor(environment.codeGenerator, PoetFactory())
    }
}

