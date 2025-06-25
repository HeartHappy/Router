package com.hearthappy.route.interceptor

import android.util.Log
import com.hearthappy.router.annotations.Interceptor
import com.hearthappy.router.core.Courier
import com.hearthappy.router.interfaces.IInterceptor
import com.hearthappy.router.interfaces.InterceptorCallback

@Interceptor(priority = 1, name = "RoomInterceptor") class RoomInterceptor : IInterceptor {
    override fun intercept(courier: Courier, callback: InterceptorCallback) {
        if (courier.getPath() == "/model/ui") {
            Log.d("RoomInterceptor", "intercept: Execute Interceptor 2")
            callback.onContinue(courier)
        }
        callback.onContinue(courier)
    }
}