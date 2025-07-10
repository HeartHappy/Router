package com.hearthappy.router.datahandler

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.hearthappy.router.enums.RouteType
import com.hearthappy.router.ext.RouterTypeNames.Activity
import com.hearthappy.router.ext.RouterTypeNames.AppCompatActivity
import com.hearthappy.router.ext.RouterTypeNames.BroadcastReceiver
import com.hearthappy.router.ext.RouterTypeNames.Fragment
import com.hearthappy.router.ext.RouterTypeNames.PathReplaceService
import com.hearthappy.router.ext.RouterTypeNames.ProviderService
import com.hearthappy.router.ext.RouterTypeNames.SerializationService
import com.hearthappy.router.ext.RouterTypeNames.Service
import com.hearthappy.router.model.RouteMeta
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName


fun String.reRouterName(): String {
    return this.split("/").joinToString("") { it.replaceFirstChar { rfc -> rfc.uppercaseChar() } }.replaceFirstChar { it.uppercase() }
}


// 定义目标类型与RouteType的映射表（按匹配优先级排序）
val typeMappings = listOf( // 接口类映射（优先级高于普通类）
    SerializationService to RouteType.SERVICE_PROVIDER, PathReplaceService to RouteType.SERVICE_PROVIDER, ProviderService to RouteType.SERVICE_PROVIDER, // 普通类映射（按继承层级从具体到抽象排序）
    AppCompatActivity to RouteType.ACTIVITY, Activity to RouteType.ACTIVITY, Service to RouteType.SERVICE, Fragment to RouteType.FRAGMENT, BroadcastReceiver to RouteType.BROADCAST)

fun KSClassDeclaration.convertType(): RouteMeta {
    // 获取直接父类（仅类类型，排除接口）
    val directParent = superTypes.mapNotNull { it.resolve().declaration as? KSClassDeclaration }.firstOrNull { it.classKind == ClassKind.CLASS || it.classKind == ClassKind.INTERFACE } // 过滤接口，只保留类类型父类
        ?.toClassName()

    // 构造返回值（parent为直接父类，superClass为匹配到的目标超类）
    // 获取最后一个超类型
    val superClass = getAllSuperTypes().lastOrNull { typeMappings.any { (targetClass, _) -> it.toClassName() == targetClass } }?.toClassName()

    // 确定RouteType（未匹配时为UNKNOWN）
    val routeType = superClass?.let { matchedClass ->
        typeMappings.first { (targetClass, _) -> targetClass == matchedClass }.second
    } ?: RouteType.UNKNOWN

    return RouteMeta(routeType = routeType, parent = directParent, superClass = superClass ?: ClassName.bestGuess("com.hearthappy.router.UNKNOWN"))
}

