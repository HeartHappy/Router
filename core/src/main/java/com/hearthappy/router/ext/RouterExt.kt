package com.hearthappy.router.ext

import android.net.Uri
import android.os.Bundle

fun String.renaming(): String {
    val replaceFirstChar = this.split("/").joinToString("") { it.replaceFirstChar { rfc -> rfc.uppercaseChar() } }.replaceFirstChar { it.uppercase() }
    return "Path$$".plus(replaceFirstChar)
}

fun String.reRouterName(): String {
    return "Router$$${this.substringAfterLast(".")}"
}

/**
 * 将 Uri 中的查询参数智能转换为 Bundle（自动推断类型）
 * @receiver Uri 需要解析的 Uri 对象
 * @param bundle Bundle 包含多种类型参数的 Bundle
 */
fun Uri.toSmartBundle(bundle: Bundle) {
    val queryKeys = this.queryParameterNames // 获取所有参数名

    for (key in queryKeys) {
        val rawValue = this.getQueryParameter(key) ?: continue // 参数值为空时跳过
        val decodedValue = Uri.decode(rawValue) // 解码特殊字符（如空格、中文）

        // 按优先级尝试转换为具体类型（int → boolean → long → float → String）
        when { // 尝试转换为 int（支持 "18" → 18）
            decodedValue.matches(Regex("-?\\d+")) -> {
                bundle.putInt(key, decodedValue.toInt())
            } // 尝试转换为 boolean（支持 "true"/"false" 不区分大小写）
            decodedValue.equals("true", ignoreCase = true) -> {
                bundle.putBoolean(key, true)
            }
            decodedValue.equals("false", ignoreCase = true) -> {
                bundle.putBoolean(key, false)
            } // 尝试转换为 long（支持 "1234567890" → 1234567890L）
            decodedValue.matches(Regex("-?\\d+")) -> {
                bundle.putLong(key, decodedValue.toLong())
            } // 尝试转换为 float（支持 "3.14" → 3.14f）
            decodedValue.matches(Regex("-?\\d+\\.?\\d*")) -> {
                try {
                    bundle.putFloat(key, decodedValue.toFloat())
                } catch (e: NumberFormatException) { // 无法转换为 float，降级为 String
                    bundle.putString(key, decodedValue)
                }
            } // 其他情况，默认存储为 String
            else -> {
                bundle.putString(key, decodedValue)
            }
        }
    }
}