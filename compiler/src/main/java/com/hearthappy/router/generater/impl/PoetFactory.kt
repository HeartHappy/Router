package com.hearthappy.router.generater.impl

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSFile
import com.hearthappy.router.constant.Constant.APP
import com.hearthappy.router.ext.RouterTypeNames
import com.hearthappy.router.generater.IPoetFactory
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo


/**
 * @Author ChenRui
 * @Email  1096885636@qq.com
 * @Date  2024/9/27 15:07
 * @description 生成工厂适配器
 */
open class PoetFactory : IPoetFactory {
    override fun createFileSpec(fileName: String, generatePackagePath: String): FileSpec.Builder {
        return FileSpec.builder(generatePackagePath, fileName)
    }

    override fun createObjectSpec(className: String): TypeSpec.Builder {
        return TypeSpec.objectBuilder(className)
    }

    override fun createClassSpec(className: String): TypeSpec.Builder {
        return TypeSpec.classBuilder(className)
    }

    override fun createClassSpec(className: String, constructorParameters: List<ParameterSpec>, superClassName: ClassName?, isAddConstructorProperty: Boolean): TypeSpec.Builder {
        return TypeSpec.classBuilder(className).apply {
            if (constructorParameters.isNotEmpty()) { //创建构造参数
                primaryConstructor(FunSpec.constructorBuilder().addParameters(constructorParameters).build())

                if (isAddConstructorProperty) { //创建构造参数属性，（即：添加val ，提供内部引用）
                    for (cp in constructorParameters) addProperty(createPropertySpec(cp.name, cp.type, receiver = null, false, codeBlock = CodeBlock.of(cp.name), KModifier.PRIVATE))

                    for (cp in constructorParameters) if (cp.name == APP) addSuperclassConstructorParameter(cp.name)
                }
            }
            superClassName?.let { superclass(it) }
        }
    }

    override fun createPropertySpec(propertyName: String, typeName: TypeName, receiver: TypeName?, isDelegate: Boolean, codeBlock: CodeBlock, vararg modifiers: KModifier): PropertySpec {
        return PropertySpec.builder(propertyName, typeName).apply {
            receiver?.let { receiver(it) }
            if (isDelegate) delegate(codeBlock) else initializer(codeBlock)
        }.addModifiers(*modifiers).build()
    }

    override fun FileSpec.Builder.addSpecProperty(propertyName: String, typeName: TypeName, receiver: TypeName?, isDelegate: Boolean, delegate: CodeBlock, vararg modifiers: KModifier) {
        addProperty(createPropertySpec(propertyName, typeName, receiver, isDelegate, delegate, *modifiers))
    }

    override fun TypeSpec.Builder.addSpecProperty(propertyName: String, typeName: TypeName, receiver: TypeName?, isDelegate: Boolean, delegate: CodeBlock, vararg modifiers: KModifier) {
        addProperty(createPropertySpec(propertyName, typeName, receiver, isDelegate, delegate, *modifiers))
    }


    override fun FileSpec.Builder.addFunctionsSpec(methodName: String, parameters: List<ParameterSpec>, methodBody: FunSpec.Builder.() -> Unit) {
        FunSpec.builder(methodName).addParameters(parameters).apply(methodBody)
    }

    override fun TypeSpec.Builder.addFunctionsSpec(methodName: String, parameters: List<ParameterSpec>, methodBody: FunSpec.Builder.() -> Unit) {
        FunSpec.builder(methodName).addParameters(parameters).apply(methodBody)
    }

    override fun FileSpec.Builder.buildAndWrite(classSpec: TypeSpec?, containingFile: KSFile, codeGenerator: CodeGenerator) {
        classSpec?.let { addType(it) }
        build().writeTo(codeGenerator, Dependencies(false, containingFile))

    }

    override fun TypeSpec.Builder.buildAndWrite(fileName: String, generatePackagePath: String, containingFile: KSFile, codeGenerator: CodeGenerator) {
        FileSpec.builder(generatePackagePath, fileName).buildAndWrite(this.build(), containingFile, codeGenerator)
    }
}