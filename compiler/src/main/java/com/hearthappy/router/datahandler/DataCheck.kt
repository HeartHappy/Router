package com.hearthappy.router.datahandler

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import java.util.regex.Pattern

object DataCheck {

    fun isParameter(paramName: String?, paramType: String) = !(paramName == "other" || paramType == "Any")


    fun isFunction(functionName: String) = !listOf("equals", "hashCode", "toString").contains(functionName) //        !(functionName == "equals" || functionName == "hashCode" || functionName == "toString" || functionName == "component")

    fun isReturnType(returnType: String) = !listOf("Boolean", "Int", "Float", "Double", "String").contains(returnType) //        !(returnType == "Boolean" || returnType == "Int" || returnType == "Float" || returnType == "Double" || returnType == "String")

    fun String?.isBasicDataTypes() = listOf("kotlin.Boolean", "kotlin.Boolean?", "kotlin.Int", "kotlin.Int?", "kotlin.Float", "kotlin.Float?", "kotlin.String", "kotlin.String?", "kotlin.Double", "kotlin.Double?", "kotlin.Long", "kotlin.Long?").contains(this)

    //检查函数是否包含注解
    fun KSFunctionDeclaration.isContainsAnnotation() = this.annotations.count() > 0

    fun Sequence<KSAnnotated>.isEmpty() = this.count() == 0


    fun String.hasGeneric(): Boolean = this.contains("<") && this.contains(">")

    fun String.isString() = when (this) {
        "kotlin.String" -> true
        "kotlin.String?" -> true
        else -> false
    }

    fun String.getGenericType(): String {
        val pattern = "<([^<>]*)>"
        val r = Pattern.compile(pattern)
        val m = r.matcher(this)
        return if (m.find()) {
            m.group(1)
        } else ""
    }

    fun isCollectionType(typeName: String): Boolean {
        return typeName.startsWith("kotlin.collections.List") || typeName.startsWith("kotlin.collections.Set") || typeName.startsWith("kotlin.collections.Map")
    }

    fun <T1 : Any, T2 : Any> bothNonNull(a: T1?, b: T2?, block: (T1, T2) -> Unit) {
        a?.let { aNonNull ->
            b?.let { bNonNull ->
                block(aNonNull, bNonNull)
            }
        }
    }

    fun <T1 : Any, T2 : Any, T3 : Any> bothNonNull(a: T1?, b: T2?, c: T3?, block: (T1, T2, T3) -> Unit) {
        a?.let { aNonNull ->
            b?.let { bNonNull ->
                c?.let { cNonNull ->
                    block(aNonNull, bNonNull, cNonNull)
                }
            }
        }
    }
}
