package com.hearthappy.router.service.impl

import com.hearthappy.router.analysis.TargetInterceptor
import com.hearthappy.router.core.Sorter.Companion.GENERATE_ROUTER_INTERCEPTOR_PKG
import com.hearthappy.router.interfaces.IInterceptor
import com.hearthappy.router.service.ClassLoaderService
import com.hearthappy.router.service.InterceptorService

/**
 * Created Date: 2025/6/11
 * @author ChenRui
 * ClassDescription： Interceptor Service impl
 */
class InterceptorServiceImpl : InterceptorService {
    private val alphabet = ('A'..'J').map { it.toString() }
    private val interceptors by lazy { mutableListOf<InterceptorRecord>() }
    override fun init(classLoaderService : ClassLoaderService) {
        alphabet.forEach {
            try {
                val ti = classLoaderService.getAnnotation(TargetInterceptor::class.java, GENERATE_ROUTER_INTERCEPTOR_PKG.plus("Interceptor$$").plus(it))
                val instance = classLoaderService.getInstance(ti.clazz.java.name)
                if (IInterceptor::class.java.isAssignableFrom(instance.javaClass)) {
                    addRouterInterceptor(InterceptorRecord(instance as IInterceptor, ti.priority, ti.name))
                }
            } catch (e : Exception) {
                interceptors.sortBy { ipt -> ipt.priority }
                return
            }
        }
    }

    override fun addRouterInterceptor(interceptor : InterceptorRecord) {
        interceptors.add(interceptor)
    }

    override fun getRouterInterceptors() : List<InterceptorRecord> {
        return interceptors
    }

    data class InterceptorRecord(val interceptor : IInterceptor, val priority : Int, val name : String)

}