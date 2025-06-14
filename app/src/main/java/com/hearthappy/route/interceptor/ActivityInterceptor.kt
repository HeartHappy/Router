package com.hearthappy.route.interceptor

import android.util.Log
import android.widget.Toast
import com.hearthappy.route.R
import com.hearthappy.router.annotations.Interceptor
import com.hearthappy.router.core.Mailman
import com.hearthappy.router.exception.HandlerException
import com.hearthappy.router.interfaces.IInterceptor
import com.hearthappy.router.interfaces.InterceptorCallback




@Interceptor(priority = 0, name = "InterceptorName") class ActivityInterceptor : IInterceptor {
    override fun intercept(mailman: Mailman, callback: InterceptorCallback) {
        if (mailman.getPath() == "/model/ui") {

            if (interceptorSwitch) {
                /**
                 * The smaller the priority value, the higher the priority.
                 * If it is used with onInterrupt and an exception is thrown,
                 * the subsequent interceptors will be interrupted.
                 */
                val msg = "Interrupt low-level interceptors"
                callback.onInterrupt(HandlerException(msg))
                Toast.makeText(mailman.getContext(), msg, Toast.LENGTH_SHORT).show()
            }else{
                callback.onContinue(mailman.withTransition(R.anim.window_bottom_in, R.anim.window_bottom_out))
            }
        } else {
            callback.onContinue(mailman)
        }
    }

    companion object {
        private const val TAG = "ActivityInterceptor"
        var interceptorSwitch = true
    }
}