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

class RouterVisitor(private val routes : MutableMap<String, RouterInfo>) : KSVisitorVoid() {
    override fun visitClassDeclaration(classDeclaration : KSClassDeclaration, data : Unit) {
        super.visitClassDeclaration(classDeclaration, data)
        processRouteClass(classDeclaration)
    }

    private fun processRouteClass(classDeclaration : KSClassDeclaration) {
        val routeAnnotation = classDeclaration.annotations.find { it.annotationType.resolve().declaration.qualifiedName?.asString() == Route::class.qualifiedName }

        val path = routeAnnotation?.arguments?.first { it.name?.asString() == "path" }?.value as String

        val className = classDeclaration.qualifiedName?.asString() ?: ""
        val routerInfo = RouterInfo()
        routerInfo.clazz = className
        routerInfo.containingFile = classDeclaration.containingFile
        routerInfo.routerMeta = classDeclaration.convertType()
        val needImport = mutableSetOf<ClassName>()
        classDeclaration.getAllProperties().forEach { property ->

            property.annotations.forEach { annotation ->
                if (annotation.annotationType.resolve().declaration.qualifiedName?.asString() == Autowired::class.qualifiedName) {
                    val name = annotation.arguments.firstOrNull { it.name?.asString() == "name" }?.value as? String
                    val fieldName = property.simpleName.asString()
                    val paramName = name?.takeIf { it.isNotEmpty() }?.run { this } ?: fieldName
                    val resolve = property.type.resolve()
                    val declaration = resolve.declaration
                    val qualifiedName = declaration.qualifiedName?.asString()
                    val autowiredParam = if (DataInspector.isCollectionType(qualifiedName.toString())) {
                        CollectionsTypeNames.ArrayList
                    } else {
                        resolve.toClassName()
                    }
                    val isImport = autowiredParam.packageName!="kotlin" //如果不是基本类型就需要导包
                    val bundleMethod = DataInspector.getBundleMethod(resolve)
                    val isSystem = DataInspector.isSystem(autowiredParam)
                    val isLateInit = isLateinit(property)
                    val injectParams = if(declaration is KSClassDeclaration){
                        val directParent = declaration.superTypes
                            .mapNotNull { it.resolve().declaration as? KSClassDeclaration }
                            .firstOrNull { it.classKind == ClassKind.CLASS ||it.classKind== ClassKind.INTERFACE }
                            ?.toClassName()
                        ParamsInfo(name = paramName, fieldName = fieldName, type = autowiredParam, autowiredType = DataInspector.getBundleMethod(resolve),directParent, isSystemPkg = isSystem, isLateinit = isLateInit)
                    }else{
                        ParamsInfo(name = paramName, fieldName = fieldName, type = autowiredParam, autowiredType = DataInspector.getBundleMethod(resolve), isSystemPkg = isSystem, isLateinit = isLateInit)
                    }

                    routerInfo.params.add(injectParams)
                    if (isImport) {
                        needImport.add(autowiredParam)
                        if (bundleMethod.contains("getObject")) {
                            needImport.add(RouterTypeNames.Router)
                            needImport.add(RouterTypeNames.SerializationService)
                        }
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