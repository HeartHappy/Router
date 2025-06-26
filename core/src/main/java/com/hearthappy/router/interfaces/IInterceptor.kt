package com.hearthappy.router.interfaces

import com.hearthappy.router.launcher.Sorter

interface IInterceptor {

    fun intercept(sorter: Sorter, callback: InterceptorCallback)
}