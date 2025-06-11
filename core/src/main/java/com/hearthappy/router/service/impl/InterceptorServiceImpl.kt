package com.hearthappy.router.service.impl

import android.util.Log
import com.hearthappy.router.analysis.TargetInterceptor
import com.hearthappy.router.core.Mailman.Companion.GENERATE_ROUTER_INTERCEPTOR_PKG
import com.hearthappy.router.interfaces.IInterceptor
import com.hearthappy.router.service.InterceptorService

/**
 * Created Date: 2025/6/11
 * @author ChenRui
 * ClassDescription： Interceptor Service impl
 */
class InterceptorServiceImpl : InterceptorService {
    private val alphabet = ('A'..'J').map { it.toString() }
    private val interceptors by lazy { mutableListOf<InterceptorRecord>() }
    override fun init() {
        alphabet.forEach {
            try {
                val destination = GENERATE_ROUTER_INTERCEPTOR_PKG.plus("Interceptor$$").plus(it)
                val interceptor = Class.forName(destination)
                val targetInterceptorAnt = interceptor.getAnnotation(TargetInterceptor::class.java)
                if (targetInterceptorAnt != null) {
                    targetInterceptorAnt.priority
                    val targetInterceptor = Class.forName(targetInterceptorAnt.clazz.java.name)
                    val instance = targetInterceptor.newInstance()
                    if (IInterceptor::class.java.isAssignableFrom(instance.javaClass)) {
                        addRouterInterceptor(InterceptorRecord(instance as IInterceptor, targetInterceptorAnt.priority, targetInterceptorAnt.name))
                    }
                }
            } catch (e: Exception) {
                interceptors.sortBy { it.priority }.also { Log.d(TAG, "init111: ${interceptors.toList()}") }
                Log.d(TAG, "init222: ${interceptors.toList()}")
                return
            }
        }
    }

    override fun addRouterInterceptor(interceptor: InterceptorRecord) {
        interceptors.add(interceptor)
    }

    override fun getRouterInterceptors(): List<InterceptorRecord> {
        return interceptors
    }

    data class InterceptorRecord(val interceptor: IInterceptor, val priority: Int, val name: String)

    companion object {
        private const val TAG = "InterceptorServiceImpl"
    }
}