package com.hearthappy.router.datahandler

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeReference
import com.hearthappy.router.enums.RouteType
import com.hearthappy.router.ext.RouterTypeNames.Activity
import com.hearthappy.router.ext.RouterTypeNames.AppCompatActivity
import com.hearthappy.router.ext.RouterTypeNames.BroadcastReceiver
import com.hearthappy.router.ext.RouterTypeNames.Fragment
import com.hearthappy.router.ext.RouterTypeNames.PathReplaceService
import com.hearthappy.router.ext.RouterTypeNames.ProviderService
import com.hearthappy.router.ext.RouterTypeNames.SerializationService
import com.hearthappy.router.ext.RouterTypeNames.Service
import com.hearthappy.router.logger.KSPLog
import com.hearthappy.router.model.RouteMeta
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName


fun String.reRouterName(): String {
    return this.split("/").joinToString("") { it.replaceFirstChar { rfc -> rfc.uppercaseChar() } }.replaceFirstChar { it.uppercase() }
}


// 解析类型别名的实际类
fun KSType.toResolvedClassName(): TypeName {
    return when (val declaration = declaration) {
        is KSClassDeclaration -> declaration.toClassName()
        is KSTypeAlias -> {
            // 获取类型别名的 underlying type
           declaration.type.toTypeName()
        }
        else -> error("Unsupported type: $declaration")
    }
}

// 定义目标类型与RouteType的映射表（按匹配优先级排序）
val typeMappings = listOf(
    // 接口类映射（优先级高于普通类）
    SerializationService to RouteType.SERVICE_PROVIDER,
    PathReplaceService to RouteType.SERVICE_PROVIDER,
    ProviderService to RouteType.SERVICE_PROVIDER,
    // 普通类映射（按继承层级从具体到抽象排序）
    AppCompatActivity to RouteType.ACTIVITY,
    Activity to RouteType.ACTIVITY,
    Service to RouteType.SERVICE,
    Fragment to RouteType.FRAGMENT,
    BroadcastReceiver to RouteType.BROADCAST
)
fun KSClassDeclaration.convertType(): RouteMeta {
    // 获取直接父类（仅类类型，排除接口）
    val directParent = superTypes
        .mapNotNull { it.resolve().declaration as? KSClassDeclaration }
        .firstOrNull { it.classKind == ClassKind.CLASS ||it.classKind== ClassKind.INTERFACE } // 过滤接口，只保留类类型父类
        ?.toClassName()

    // 遍历所有超类型（包括直接和间接），查找第一个匹配的目标类型
    val matchedSuper = getAllSuperTypes()
        .map { it.toClassName() }
        .firstOrNull { className ->
            typeMappings.any { (targetClass, _) -> className == targetClass }
        }

    // 确定RouteType（未匹配时为UNKNOWN）
    val routeType = matchedSuper?.let { matchedClass ->
        typeMappings.first { (targetClass, _) -> targetClass == matchedClass }.second
    } ?: RouteType.UNKNOWN

    // 构造返回值（parent为直接父类，superClass为匹配到的目标超类）
//    KSPLog.print("routeType:$routeType, parent:$directParent, superClass:$matchedSuper")
    return RouteMeta(
        routeType = routeType,
        parent = directParent,
        superClass = matchedSuper ?: ClassName.bestGuess("com.hearthappy.router.UNKNOWN")
    )
}

