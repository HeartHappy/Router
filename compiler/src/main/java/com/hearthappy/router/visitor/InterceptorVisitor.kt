package com.hearthappy.router.visitor

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.hearthappy.router.annotations.Interceptor
import com.hearthappy.router.model.InterceptorInfo

class InterceptorVisitor(private val interceptors: MutableList<InterceptorInfo>) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        super.visitClassDeclaration(classDeclaration, data)
        val intAnt = classDeclaration.annotations.first { it.annotationType.resolve().declaration.qualifiedName?.asString() == Interceptor::class.qualifiedName }
        val interceptorInfo = InterceptorInfo()
        interceptorInfo.clazz =classDeclaration.qualifiedName?.asString() ?: ""
        interceptorInfo.containingFile=classDeclaration.containingFile
        for (argument in intAnt.arguments) {
            when (argument.name?.asString()) {
                "priority" -> interceptorInfo.priority = argument.value as Int
                "name" -> interceptorInfo.name = argument.value as String
                else -> {}
            }
        }
        interceptors.add(interceptorInfo)
    }
}