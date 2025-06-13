package com.hearthappy.router.datahandler

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueArgument
import com.hearthappy.router.constant.Constant
import com.hearthappy.router.ext.RouterTypeNames
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toClassName


fun <R : Any> List<KSValueArgument>.findArgsValue(paramName: String): R {
    for (arg in this) {
        if (arg.name?.asString() == paramName) {
            @Suppress("UNCHECKED_CAST") return arg.value as R
        }
    }
    throw IllegalArgumentException("No argument found with name: $paramName")
}


fun Sequence<KSAnnotation>.findSpecifiedAnt(vararg annotationNames: String) =
    this.find { annotationNames.contains(it.shortName.asString()) }

fun String.bindSuffix() = this.removePrefix("Bind")

/**
 * 转换首字母为大写
 * @receiver String
 * @return String
 */
fun String.convertFirstChar(): String =
    this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

/**
 * 将注解类型转换成属性前缀
 * @receiver String BindLiveData\BindStateFlow
 * @return String ld\sf
 */
fun String.convertToPrefix(): String {
    return when (this) {
        "BindLiveData" -> "ld"
        "BindStateFlow" -> "sf"
        else -> throw IllegalArgumentException("Unknown binding annotation type: $this")
    }
}

fun String.privatePropertyName() = "_${this}"

fun String.className2PropertyName() = this.replaceFirstChar { it.lowercaseChar() }

/**
 * 转换成全大写和下划线组合名称
 * @receiver String
 * @return String
 */
fun String.reConstName(): String { // 使用正则表达式将小写字母和大写字母分开
    return replace(Regex("([a-z])([A-Z])"), "$1_$2") // 将整个字符串转换为大写
        .uppercase()
}

/**
 * 转换成去下划线和驼峰命名规则
 * @receiver String user_info
 * @return String userInfoDataStore
 */
fun String.rename(): String {
    return convertToCamelCase(this).plus("DataStore")
}


/**
 * 转换成文件名格式。例如：UserInfoExt
 * @receiver String
 * @return String
 */
fun String.reFileName(): String {
    return this.split("_").joinToString("") { it.replaceFirstChar { rfc -> rfc.uppercaseChar() } }
        .replaceFirstChar { it.uppercase() }.plus("Ext")
}

fun String.rePreferencesKeysName(): String {
    return this.split("_").joinToString("") { it.replaceFirstChar { rfc -> rfc.uppercaseChar() } }
        .replaceFirstChar { it.uppercase() }.plus("Keys")
}

fun String.reRouterName(): String {
    return this.split("/").joinToString("") { it.replaceFirstChar { rfc -> rfc.uppercaseChar() } }
        .replaceFirstChar { it.uppercase() }
}

fun convertToCamelCase(input: String): String {
    return input.split("_")
        .joinToString("") { joinStr -> joinStr.replaceFirstChar { it.uppercaseChar() } }
        .replaceFirstChar { it.lowercaseChar() }
}


// 扩展函数：判断一个类是否是另一个类的子类
fun isActivity(supperClass: TypeName): Boolean {
    return supperClass.toString().contains(RouterTypeNames.Activity.simpleName)
}

fun isService(supperClass: TypeName): Boolean {
    return supperClass.toString().contains(RouterTypeNames.Service.simpleName)
}

fun isFragment(supperClass: TypeName): Boolean {
    return supperClass.toString().contains(RouterTypeNames.Fragment.simpleName)
}

fun isBroadcastReceiver(supperClass: TypeName): Boolean {
    return supperClass.toString().contains(RouterTypeNames.BroadcastReceiver.simpleName)
}

fun isServiceProvider(supperClass: TypeName):Boolean{
    return  RouterTypeNames.ServiceProvider.javaClass.isAssignableFrom(supperClass.javaClass)
}


// 类型转换逻辑
fun KSClassDeclaration.convertType(): String {
    for (allSuperType in getAllSuperTypes()) {
        val className = allSuperType.toClassName()
        when (className) {
            RouterTypeNames.ServiceProvider -> return Constant.ROUTER_TYPE_SERVICE_PROVIDER
            RouterTypeNames.AppCompatActivity, RouterTypeNames.Activity -> return Constant.ROUTER_TYPE_ACTIVITY
            RouterTypeNames.Service -> return Constant.ROUTER_TYPE_SERVICE
            RouterTypeNames.Fragment -> return Constant.ROUTER_TYPE_FRAGMENT
            RouterTypeNames.BroadcastReceiver -> return Constant.ROUTER_TYPE_BROADCAST
        }

    }
    return "RouteType.UNKNOWN"
}

