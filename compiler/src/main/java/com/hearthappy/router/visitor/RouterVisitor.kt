package com.hearthappy.router.visitor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.datahandler.convertType
import com.hearthappy.router.enums.ParamInfo
import com.hearthappy.router.model.RouterInfo
import com.squareup.kotlinpoet.ksp.toTypeName

class RouterVisitor(private val routes: MutableMap<String, RouterInfo>) : KSVisitorVoid() {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        super.visitClassDeclaration(classDeclaration, data)
        processRouteClass(classDeclaration)
    }

    private fun processRouteClass(classDeclaration: KSClassDeclaration) {
        val routeAnnotation = classDeclaration.annotations.find { it.annotationType.resolve().declaration.qualifiedName?.asString() == Route::class.qualifiedName }

        val path = routeAnnotation?.arguments?.first { it.name?.asString() == "path" }?.value as String

        val className = classDeclaration.qualifiedName?.asString() ?: ""
        val routerInfo = RouterInfo()
        routerInfo.clazz = className
        routerInfo.containingFile = classDeclaration.containingFile
        routerInfo.supperType = classDeclaration.superTypes.first().toTypeName().convertType()
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