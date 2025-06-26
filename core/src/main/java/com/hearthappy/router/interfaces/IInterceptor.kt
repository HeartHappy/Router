package com.hearthappy.router.interfaces

import com.hearthappy.router.core.Sorter

interface IInterceptor {

    fun intercept(sorter: Sorter, callback: InterceptorCallback)
}