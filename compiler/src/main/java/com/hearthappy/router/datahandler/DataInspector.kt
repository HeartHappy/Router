package com.hearthappy.router.datahandler

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName

object DataInspector {
    const val EMPTY_STRING = ""


    fun isCollectionType(typeName: String): Boolean {
        return typeName.startsWith("kotlin.collections.ArrayList") || typeName.startsWith("kotlin.collections.MutableList") || typeName.startsWith("kotlin.collections.List") || typeName.startsWith("kotlin.collections.Set") || typeName.startsWith("kotlin.collections.Map")
    }

    fun getBundleMethod(resolve: KSType): String {
        val declaration = resolve.declaration
        val typeName = declaration.qualifiedName?.asString() ?: ""
        val firstTypeArg = resolve.arguments.firstOrNull()?.type?.resolve()
        val firstTypeName = firstTypeArg?.declaration?.qualifiedName?.asString() ?: ""
        return if (resolve.arguments.isNotEmpty()) {
            getGenericBundleMethod(typeName, firstTypeName)
        } else {
            when (typeName) {
                "kotlin.String" -> "getString"
                "kotlin.Int" -> "getInt"
                "kotlin.Boolean" -> "getBoolean"
                "kotlin.Short" -> "getShort"
                "kotlin.Long" -> "getLong"
                "kotlin.Float" -> "getFloat"
                "kotlin.Double" -> "getDouble"
                "kotlin.Char" -> "getChar"
                "kotlin.Byte" -> "getByte"
                "kotlin.CharSequence" -> "getCharSequence"
                "android.os.Parcelable" -> "getParcelable"
                "java.io.Serializable" -> "getSerializable"
                "android.os.Bundle" -> "getBundle"
                "kotlin.ByteArray" -> "getByteArray"
                "kotlin.CharArray" -> "getCharArray"
                "kotlin.ShortArray" -> "getShortArray"
                "kotlin.IntArray" -> "getIntArray"
                "kotlin.LongArray" -> "getLongArray"
                "kotlin.FloatArray" -> "getFloatArray"
                "kotlin.DoubleArray" -> "getDoubleArray"
                "kotlin.BooleanArray" -> "getBooleanArray"
                else -> "getObject"
            }
        }

    }

    private fun getGenericBundleMethod(typeName: String, firstTypeName: String): String {

        return when {
            typeName == "kotlin.Array" -> when (firstTypeName) {
                "kotlin.CharSequence" -> "getCharSequenceArray"
                "android.os.Parcelable" -> "getParcelableArray"
                else -> "getStringArray"
            }
            typeName == "kotlin.collections.ArrayList" -> when (firstTypeName) {
                "android.os.Parcelable" -> "getParcelableArrayList"
                "kotlin.Int" -> "getIntegerArrayList"
                "kotlin.CharSequence" -> "getCharSequenceArrayList"
                else -> "getStringArrayList"
            }
            typeName == "android.util.SparseArray" && firstTypeName == "android.os.Parcelable" -> {
                "getSparseParcelableArray"
            }
            else -> "get"
        }
    }


    fun isSystem(className: ClassName):Boolean {
            return className.toString() in primitiveTypes || className.toString() in standardBundleTypes || className.toString() in arrayTypes || className.toString() in collectionTypes
    }
    fun toDefaultValue(typeName: String): Any {
        return when (typeName) {
            "kotlin.Int" -> -1
            "kotlin.Boolean" -> false
            "kotlin.Short" -> (-1).toShort()
            "kotlin.Long" -> -1L
            "kotlin.Float" -> -1f
            "kotlin.Double" -> -1.0
            "kotlin.Char" -> '\u0000' // NULL字符
            "kotlin.Byte" -> (-1).toByte()
            else -> "\"\""//String,CharSequence
        }
    }
    // Define sets of standard types
    internal val primitiveTypes = setOf("kotlin.String", "kotlin.Int", "kotlin.Boolean", "kotlin.Short", "kotlin.Long", "kotlin.Float", "kotlin.Double", "kotlin.Char", "kotlin.Byte", "kotlin.CharSequence")

    private val standardBundleTypes = setOf("android.os.Parcelable", "java.io.Serializable", "android.os.Bundle")

    private val arrayTypes = setOf("kotlin.Array", "kotlin.ByteArray", "kotlin.ShortArray", "kotlin.IntArray", "kotlin.LongArray", "kotlin.FloatArray", "kotlin.DoubleArray", "kotlin.BooleanArray", "kotlin.CharArray")

    private val collectionTypes = setOf("java.util.ArrayList","kotlin.collections.ArrayList", "android.util.SparseArray")
}
