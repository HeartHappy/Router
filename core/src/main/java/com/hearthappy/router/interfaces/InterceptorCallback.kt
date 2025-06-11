package com.hearthappy.router.interfaces

/**
 * Created Date: 2025/6/10
 * @author ChenRui
 * ClassDescription：The callback of interceptor.
 */
interface InterceptorCallback {
    /**
     * The interceptor continues the routing process
     */
    fun onContinue(director: IDirector)

    /**
     * The interceptor interrupts the routing process and only interrupts it when an exception is thrown
     */
    fun onInterrupt(exception: Throwable?)
}