package com.hearthappy.route.interceptor

import android.util.Log
import com.hearthappy.common_api.RouterPath
import com.hearthappy.router.annotations.Interceptor
import com.hearthappy.router.launcher.Sorter
import com.hearthappy.router.interfaces.IInterceptor
import com.hearthappy.router.interfaces.InterceptorCallback

@Interceptor(priority = 1, name = "RoomInterceptor") class RoomInterceptor : IInterceptor {
    override fun intercept(sorter: Sorter, callback: InterceptorCallback) {
        if (sorter.getPath() == RouterPath.CASE_ACTIVITY_FOR_RESULT) {
            Log.d("RoomInterceptor", "intercept: Execute Interceptor 2")
            callback.onContinue(sorter)
        }
        callback.onContinue(sorter)
    }
}