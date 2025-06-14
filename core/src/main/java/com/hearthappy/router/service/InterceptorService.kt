package com.hearthappy.router.service

import com.hearthappy.router.service.impl.InterceptorServiceImpl

/**
 * Created Date: 2025/6/11
 * @author ChenRui
 * ClassDescription：Intercepting services
 */
interface InterceptorService {

    fun init(classLoaderService : ClassLoaderService)

    fun addRouterInterceptor(interceptor: InterceptorServiceImpl.InterceptorRecord)

    fun getRouterInterceptors(): List<InterceptorServiceImpl.InterceptorRecord>
}