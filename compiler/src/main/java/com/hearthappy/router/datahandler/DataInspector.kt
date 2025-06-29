package com.hearthappy.router.datahandler

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.hearthappy.router.ext.RouterTypeNames
import com.hearthappy.router.ext.RouterTypeNames.Activity
import com.hearthappy.router.ext.RouterTypeNames.BroadcastReceiver
import com.hearthappy.router.ext.RouterTypeNames.Fragment
import com.hearthappy.router.ext.RouterTypeNames.PathReplaceService
import com.hearthappy.router.ext.RouterTypeNames.SerializationService
import com.hearthappy.router.ext.RouterTypeNames.Service
import com.hearthappy.router.logger.KSPLog
import com.squareup.kotlinpoet.TypeName
import java.util.regex.Pattern

object DataInspector {
    const val EMPTY_STRING = ""

    // 扩展函数：判断一个类是否是另一个类的子类
    fun isActivity(supperClass : TypeName) : Boolean {
        return supperClass.toString().contains(Activity.simpleName)
    }

    fun isService(supperClass : TypeName) : Boolean {
        return supperClass.toString().contains(Service.simpleName)
    }

    fun isFragment(supperClass : TypeName) : Boolean {
        return supperClass.toString().contains(Fragment.simpleName)
    }

    fun isBroadcastReceiver(supperClass : TypeName) : Boolean {
        return supperClass.toString().contains(BroadcastReceiver.simpleName)
    }

    fun isServiceProvider(supperClass : KSClassDeclaration) : Boolean {
        return TypeComparator.isSameType(supperClass, SerializationService) || TypeComparator.isSameType(supperClass, PathReplaceService) || TypeComparator.isSameType(supperClass, RouterTypeNames.ProviderService)
    }

    fun isParameter(paramName : String?, paramType : String) = !(paramName == "other" || paramType == "Any")


    fun isFunction(functionName : String) = !listOf("equals", "hashCode", "toString").contains(functionName) //        !(functionName == "equals" || functionName == "hashCode" || functionName == "toString" || functionName == "component")

    fun isReturnType(returnType : String) = !listOf("Boolean", "Int", "Float", "Double", "String").contains(returnType) //        !(returnType == "Boolean" || returnType == "Int" || returnType == "Float" || returnType == "Double" || returnType == "String")

    fun String?.isBasicDataTypes() = listOf("kotlin.Boolean", "kotlin.Boolean?", "kotlin.Int", "kotlin.Int?", "kotlin.Float", "kotlin.Float?", "kotlin.String", "kotlin.String?", "kotlin.Double", "kotlin.Double?", "kotlin.Long", "kotlin.Long?").contains(this)

    //检查函数是否包含注解
    fun KSFunctionDeclaration.isContainsAnnotation() = this.annotations.count() > 0

    fun Sequence<KSAnnotated>.isEmpty() = this.count() == 0


    fun String.hasGeneric() : Boolean = this.contains("<") && this.contains(">")

    fun String.isString() = when (this) {
        "kotlin.String" -> true
        "kotlin.String?" -> true
        else -> false
    }

    fun String.getGenericType() : String {
        val pattern = "<([^<>]*)>"
        val r = Pattern.compile(pattern)
        val m = r.matcher(this)
        return if (m.find()) {
            m.group(1)
        } else ""
    }

    fun isCollectionType(typeName : String) : Boolean {
        return typeName.startsWith("kotlin.collections.ArrayList") || typeName.startsWith("kotlin.collections.MutableList") || typeName.startsWith("kotlin.collections.List") || typeName.startsWith("kotlin.collections.Set") || typeName.startsWith("kotlin.collections.Map")
    }

    fun <T1 : Any, T2 : Any> bothNonNull(a : T1?, b : T2?, block : (T1, T2) -> Unit) {
        a?.let { aNonNull ->
            b?.let { bNonNull ->
                block(aNonNull, bNonNull)
            }
        }
    }

    fun <T1 : Any, T2 : Any, T3 : Any> bothNonNull(a : T1?, b : T2?, c : T3?, block : (T1, T2, T3) -> Unit) {
        a?.let { aNonNull ->
            b?.let { bNonNull ->
                c?.let { cNonNull ->
                    block(aNonNull, bNonNull, cNonNull)
                }
            }
        }
    }

    fun getBundleMethod(type : KSTypeReference) : String {
        val declaration = type.resolve().declaration
        val typeName = declaration.qualifiedName?.asString() ?: ""
        val firstTypeArg = type.resolve().arguments.firstOrNull()?.type?.resolve()
        val firstTypeName = firstTypeArg?.declaration?.qualifiedName?.asString() ?: ""
        return if (type.resolve().arguments.isNotEmpty()) {
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

    private fun getGenericBundleMethod(typeName : String, firstTypeName : String) : String {

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

    fun needsSerialization(type : KSType) : Boolean {
        val declaration = type.declaration
        val typeName = type.declaration.qualifiedName?.asString() ?: ""

        // Skip if it's a primitive type, array, or collection
        if (type.isMarkedNullable && typeName in primitiveTypes) return false
        if (declaration is KSClassDeclaration && declaration.classKind == ClassKind.CLASS) { // Check if it's one of the standard types that Bundle handles directly
            return typeName !in standardBundleTypes && typeName !in arrayTypes && typeName !in collectionTypes
        }
        return false
    }

    // Define sets of standard types
    private val primitiveTypes = setOf("kotlin.String", "kotlin.Int", "kotlin.Boolean", "kotlin.Short", "kotlin.Long", "kotlin.Float", "kotlin.Double", "kotlin.Char", "kotlin.Byte", "kotlin.CharSequence")

    private val standardBundleTypes = setOf("android.os.Parcelable", "java.io.Serializable", "android.os.Bundle")

    private val arrayTypes = setOf("kotlin.Array", "kotlin.ByteArray", "kotlin.ShortArray", "kotlin.IntArray", "kotlin.LongArray", "kotlin.FloatArray", "kotlin.DoubleArray", "kotlin.BooleanArray", "kotlin.CharArray")

    private val collectionTypes = setOf("java.util.ArrayList", "android.util.SparseArray")
}
