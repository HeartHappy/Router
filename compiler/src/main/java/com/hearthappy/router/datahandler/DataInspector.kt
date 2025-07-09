package com.hearthappy.router.datahandler

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName

object DataInspector {
    const val EMPTY_STRING = ""

    // Define sets of standard types
    internal val primitiveTypes = setOf("kotlin.String", "kotlin.Int", "kotlin.Boolean", "kotlin.Short", "kotlin.Long", "kotlin.Float", "kotlin.Double", "kotlin.Char", "kotlin.Byte", "kotlin.CharSequence")

    private val standardBundleTypes = setOf("android.os.Parcelable", "java.io.Serializable", "android.os.Bundle")

    private val arrayTypes = setOf("kotlin.Array", "kotlin.ByteArray", "kotlin.ShortArray", "kotlin.IntArray", "kotlin.LongArray", "kotlin.FloatArray", "kotlin.DoubleArray", "kotlin.BooleanArray", "kotlin.CharArray")

    private val collectionTypes = setOf("java.util.ArrayList", "kotlin.collections.ArrayList", "android.util.SparseArray")

    private val bundleMethodCache = mutableMapOf<String, String>().apply {
        put("kotlin.String", "getString")
        put("kotlin.Int", "getInt")
        put("kotlin.Boolean", "getBoolean")
        put("kotlin.Short", "getShort")
        put("kotlin.Long", "getLong")
        put("kotlin.Float", "getFloat")
        put("kotlin.Double", "getDouble")
        put("kotlin.Char", "getChar")
        put("kotlin.Byte", "getByte")
        put("kotlin.CharSequence", "getCharSequence")
        put("android.os.Parcelable", "getParcelable")
        put("java.io.Serializable", "getSerializable")
        put("android.os.Bundle", "getBundle")
        put("kotlin.ByteArray", "getByteArray")
        put("kotlin.CharArray", "getCharArray")
        put("kotlin.ShortArray", "getShortArray")
        put("kotlin.IntArray", "getIntArray")
        put("kotlin.LongArray", "getLongArray")
        put("kotlin.FloatArray", "getFloatArray")
        put("kotlin.DoubleArray", "getDoubleArray")
        put("kotlin.BooleanArray", "getBooleanArray")
    }

    private val arrayCache = mutableMapOf<String, String>().apply {
        put("kotlin.CharSequence", "getCharSequenceArray")
        put("android.os.Parcelable", "getParcelableArray")
        put("kotlin.String", "getStringArray")
    }
    private val arrayListCache = mutableMapOf<String, String>().apply {
        put("android.os.Parcelable", "getParcelableArrayList")
        put("kotlin.Int", "getIntegerArrayList")
        put("kotlin.CharSequence", "getCharSequenceArrayList")
        put("kotlin.String", "getStringArrayList")
    }
    private val sparseArrayCache = mutableMapOf<String, String>().apply {
        put("android.os.Parcelable", "getSparseParcelableArray")
    }

    private val defaultCache = mutableMapOf<String, Any>().apply {
        put("kotlin.Int", -1)
        put("kotlin.Boolean", false)
        put("kotlin.Short", (-1).toShort())
        put("kotlin.Long", -1L)
        put("kotlin.Float", -1f)
        put("kotlin.Double", -1.0)
        put("kotlin.Char", '\u0000')
        put("kotlin.Byte", (-1).toByte())
    }

    fun isCollectionType(typeName: String): Boolean {
        return typeName.startsWith("kotlin.collections.ArrayList") || typeName.startsWith("kotlin.collections.MutableList") || typeName.startsWith("kotlin.collections.List") || typeName.startsWith("kotlin.collections.Set") || typeName.startsWith("kotlin.collections.Map")
    }

    fun getBundleMethod(resolve: KSType, qualifiedName: String): String {
        return if (resolve.arguments.isNotEmpty()) {
            val firstTypeArg = resolve.arguments.firstOrNull()?.type?.resolve()
            val firstTypeName = firstTypeArg?.declaration?.qualifiedName?.asString() ?: ""
            getGenericBundleMethod(qualifiedName, firstTypeName)
        } else {
            bundleMethodCache.getOrElse(qualifiedName) { "getObject" }
        }

    }

    private fun getGenericBundleMethod(typeName: String, firstTypeName: String): String {

        return when {
            typeName == "kotlin.Array" -> arrayCache.getOrElse(firstTypeName) { "getStringArray" }
            typeName == "kotlin.collections.ArrayList" -> arrayListCache.getOrElse(firstTypeName) { "getStringArrayList" }
            typeName == "android.util.SparseArray" && firstTypeName == "android.os.Parcelable" -> sparseArrayCache.getOrElse(firstTypeName) { "getSparseParcelableArray" }
            else -> "get"
        }
    }


    fun isSystem(className: ClassName): Boolean {
        return className.toString() in primitiveTypes || className.toString() in standardBundleTypes || className.toString() in arrayTypes || className.toString() in collectionTypes
    }

    fun toDefaultValue(typeName: String): Any {
        return defaultCache.getOrElse(typeName) { "\"\"" }
    }


}
