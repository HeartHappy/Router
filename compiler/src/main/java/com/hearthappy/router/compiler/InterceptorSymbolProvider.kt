package com.hearthappy.router.compiler

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.hearthappy.router.analysis.TargetInterceptor
import com.hearthappy.router.annotations.Interceptor
import com.hearthappy.router.constant.Constant
import com.hearthappy.router.generater.IPoetFactory
import com.hearthappy.router.generater.impl.PoetFactory
import com.hearthappy.router.logger.KSPLog
import com.hearthappy.router.model.InterceptorInfo
import com.hearthappy.router.visitor.InterceptorVisitor
import com.squareup.kotlinpoet.AnnotationSpec
import kotlin.system.measureTimeMillis

/**
 * Created Date: 2025/6/11
 * @author ChenRui
 * ClassDescription：Parse and generate interceptor files
 */
class InterceptorSymbolProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        KSPLog.init(environment.logger)
        return InterceptorProcessor(environment.codeGenerator, PoetFactory())
    }

    inner class InterceptorProcessor(private val codeGenerator: CodeGenerator, private val poetFactory: IPoetFactory) : SymbolProcessor {
        private val interceptors = mutableListOf<InterceptorInfo>()
        private val alphabet = ('A'..'J').map { it.toString() }
        override fun process(resolver: com.google.devtools.ksp.processing.Resolver): List<com.google.devtools.ksp.symbol.KSAnnotated> {
            val measureTimeMillis = measureTimeMillis {
                val interceptorSymbols = resolver.getSymbolsWithAnnotation(Interceptor::class.qualifiedName!!)
                val filterInterceptor = interceptorSymbols.filter { it.validate() }.toList()
                if (filterInterceptor.isEmpty()) return emptyList()
                filterInterceptor.filterIsInstance<KSClassDeclaration>().forEach { it.accept(InterceptorVisitor(interceptors), Unit) }
                generateInterceptor()
            }
            KSPLog.printInterceptorTook(interceptors.size, measureTimeMillis)
            return emptyList()
        }

        private fun generateInterceptor() {
            interceptors.forEachIndexed { index, info ->
                poetFactory.apply {
                    generateInterceptor(index, info)
                }
            }
        }

        private fun IPoetFactory.generateInterceptor(index: Int, info: InterceptorInfo) {
            val classPkg = info.clazz.substringBeforeLast('.')
            val simpleName = info.clazz.substringAfterLast('.')
            val fileName = "Interceptor$$".plus(alphabet[index])
            val interceptorClassSpec = createClassSpec(fileName, superClassName = null, constructorParameters = emptyList(), isAddConstructorProperty = false)
            val fileSpec = createFileSpec(fileName, Constant.GENERATE_ROUTER_INTERCEPTOR_PKG)
            KSPLog.print("interceptor:$info")
            interceptorClassSpec.addAnnotation(AnnotationSpec.builder(TargetInterceptor::class).addMember("%L::class", simpleName).addMember("%L", info.priority).addMember("\"%L\"", info.name).build())
            fileSpec.addImport(classPkg, listOf(simpleName))
            fileSpec.buildAndWrite(interceptorClassSpec.build(), info.containingFile!!, codeGenerator)
        }

    }
}