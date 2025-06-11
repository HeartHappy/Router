package com.hearthappy.router.interfaces

import com.hearthappy.router.core.Mailman

interface IInterceptor {

    fun intercept(mailman: Mailman, callback: InterceptorCallback)
}