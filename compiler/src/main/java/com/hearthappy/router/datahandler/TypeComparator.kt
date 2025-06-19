package com.hearthappy.router.datahandler

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ClassName

object TypeComparator {
    /**
     * 判断 KSClassDeclaration 是否对应目标 ClassName
     * @param ksClass KSP 中获取的类声明
     * @param targetClassName 目标 ClassName（如 SerializationService）
     */
    fun isSameType(ksClass: KSClassDeclaration, targetClassName: ClassName): Boolean {
        // 获取 KSP 类的全限定名（可能为 null，需处理）
        val ksQualifiedName = ksClass.qualifiedName?.asString() ?: return false
        // 比较全限定名
        return ksQualifiedName == targetClassName.canonicalName
    }

    /**
     * 判断 KSType（解析后的类型）是否对应目标 ClassName
     * @param ksType KSP 中解析后的类型（如 from KSTypeReference.resolve()）
     * @param targetClassName 目标 ClassName
     */
    fun isSameType(ksType: KSType, targetClassName: ClassName): Boolean {
        // 从 KSType 获取对应的类声明（可能为接口、类等）
        val ksDeclaration = ksType.declaration as? KSClassDeclaration ?: return false
        return isSameType(ksDeclaration, targetClassName)
    }

    /**
     * 判断 KSTypeReference（类型引用）是否对应目标 ClassName
     * @param typeRef KSP 中的类型引用（如类的 superTypes 中的元素）
     * @param targetClassName 目标 ClassName
     */
    fun isSameType(typeRef: KSTypeReference, targetClassName: ClassName): Boolean {
        // 解析类型引用为具体类型
        val resolvedType = typeRef.resolve()
        return isSameType(resolvedType, targetClassName)
    }
}