package com.hearthappy.router.visitor

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.hearthappy.router.annotations.Autowired
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.datahandler.DataInspector
import com.hearthappy.router.datahandler.convertType
import com.hearthappy.router.ext.CollectionsTypeNames
import com.hearthappy.router.ext.RouterTypeNames
import com.hearthappy.router.model.ParamsInfo
import com.hearthappy.router.model.RouterInfo
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

class RouterVisitor(val routes: MutableMap<String, RouterInfo>) : KSVisitorVoid() {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        super.visitClassDeclaration(classDeclaration, data)
        processRouteClass(classDeclaration)
    }

    private fun processRouteClass(classDeclaration: KSClassDeclaration) { //        val routeAnnotation = classDeclaration.annotations.find { it.annotationType.resolve().declaration.qualifiedName?.asString() == Route::class.qualifiedName }
        // 优化后
        val routeAnnotation = classDeclaration.annotations.firstOrNull { annotation -> (annotation.annotationType.resolve().declaration as? KSClassDeclaration)?.qualifiedName?.asString() == Route::class.qualifiedName }
        val path = routeAnnotation?.arguments?.first { it.name?.asString() == "path" }?.value as String

        val className = classDeclaration.qualifiedName?.asString() ?: ""
        val routerInfo = RouterInfo()
        routerInfo.clazz = className
        routerInfo.containingFile = classDeclaration.containingFile
        routerInfo.routerMeta = classDeclaration.convertType()
        val needImport = mutableSetOf<ClassName>()
        //优化：过滤不需要的属性
        classDeclaration.getAllProperties().filter { prop ->
                prop.annotations.any { ann -> (ann.annotationType.resolve().declaration as? KSClassDeclaration)?.qualifiedName?.asString() == Autowired::class.qualifiedName }
            }.forEach { property ->

                property.annotations.forEach { annotation ->
                    val name = annotation.arguments.firstOrNull { it.name?.asString() == "name" }?.value as? String
                    val fieldName = property.simpleName.asString()
                    val paramName = name?.takeIf { it.isNotEmpty() }?.run { this } ?: fieldName
                    val resolve = property.type.resolve()
                    val declaration = resolve.declaration
                    val qualifiedName = declaration.qualifiedName?.asString()?:""
                    val autowiredParam = if (DataInspector.isCollectionType(qualifiedName)) {
                        CollectionsTypeNames.ArrayList
                    } else {
                        resolve.toClassName()
                    }
                    val bundleMethod = DataInspector.getBundleMethod(resolve,qualifiedName)
                    val isSystem = DataInspector.isSystem(autowiredParam)
                    val isLateInit = isLateinit(property)
                    val injectParams = if (declaration is KSClassDeclaration) {
                        val directParent = declaration.superTypes.mapNotNull { it.resolve().declaration as? KSClassDeclaration }.firstOrNull { it.classKind == ClassKind.CLASS || it.classKind == ClassKind.INTERFACE }?.toClassName()
                        ParamsInfo(name = paramName, fieldName = fieldName, type = autowiredParam, autowiredType =bundleMethod, directParent, isSystemPkg = isSystem, isLateinit = isLateInit)
                    } else {
                        ParamsInfo(name = paramName, fieldName = fieldName, type = autowiredParam, autowiredType =bundleMethod, isSystemPkg = isSystem, isLateinit = isLateInit)
                    }

                    routerInfo.params.add(injectParams)//如果不是基本类型就需要导包
                    if (autowiredParam.packageName != "kotlin") {
                        needImport.add(autowiredParam)
                        if (bundleMethod.contains("getObject")) {
                            needImport.add(RouterTypeNames.Router)
                            needImport.add(RouterTypeNames.SerializationService)
                        }
                    }
                }

            }
        routerInfo.pkg = needImport
        routes[path] = routerInfo
    }

    // 检查属性是否为 lateinit
    private fun isLateinit(property: KSPropertyDeclaration): Boolean {
        return property.modifiers.contains(Modifier.LATEINIT) || property.isDelegated()
    }
}