package com.hearthappy.router.interfaces

import com.hearthappy.router.core.Courier

interface IInterceptor {

    fun intercept(courier: Courier, callback: InterceptorCallback)
}