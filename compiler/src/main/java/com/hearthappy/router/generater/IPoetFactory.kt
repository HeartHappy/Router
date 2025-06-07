package com.hearthappy.router.generater

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
/**
 * @author ChenRui
 * ClassDescription：Poet工厂
 */
interface IPoetFactory {

    //创建文件
    fun createFileSpec(fileName: String, generatePackagePath: String): FileSpec.Builder

    //创建对象object
    fun createObjectSpec(className: String): TypeSpec.Builder

    //创建类 class
    fun createClassSpec(className: String): TypeSpec.Builder

    /**
     *
     * @param className String
     * @param constructorParameters List<ParameterSpec> 构造参数列表
     * @param superClassName ClassName 继承父类ClassName
     * @param isAddConstructorProperty Boolean 是否添加到全局构造，提供内部使用
     * @return TypeSpec.Builder
     * @deprecated 创建构造参数复杂的类
     */
    fun createClassSpec(className: String, constructorParameters: List<ParameterSpec>, superClassName: ClassName?, isAddConstructorProperty: Boolean): TypeSpec.Builder

    //创建属性Spec
    fun createPropertySpec(propertyName: String, typeName: TypeName, receiver: TypeName?, isDelegate: Boolean, codeBlock: CodeBlock, vararg modifiers: KModifier): PropertySpec

    //添加委托属性到file
    fun FileSpec.Builder.addSpecProperty(propertyName: String, typeName: TypeName, receiver: TypeName?, isDelegate: Boolean, delegate: CodeBlock, vararg modifiers: KModifier)

    //添加委托属性到class
    fun TypeSpec.Builder.addSpecProperty(propertyName: String, typeName: TypeName, receiver: TypeName?, isDelegate: Boolean, delegate: CodeBlock, vararg modifiers: KModifier)

    //添加所有函数到class
    fun FileSpec.Builder.addFunctionsSpec(methodName: String, parameters: List<ParameterSpec>, methodBody: FunSpec.Builder.() -> Unit)

    //添加所有函数到file
    fun TypeSpec.Builder.addFunctionsSpec(methodName: String, parameters: List<ParameterSpec>, methodBody: FunSpec.Builder.() -> Unit)

    //FileSpec写入文件
    fun FileSpec.Builder.buildAndWrite(classSpec: TypeSpec?, containingFile: KSFile, codeGenerator: CodeGenerator)

    //TypeSpec写入文件
    fun TypeSpec.Builder.buildAndWrite(fileName: String, generatePackagePath: String, containingFile: KSFile, codeGenerator: CodeGenerator)
}