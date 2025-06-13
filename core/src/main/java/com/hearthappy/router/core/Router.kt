package com.hearthappy.router.core

import android.app.Activity
import android.content.Context

/**
 * Created Date: 2025/6/10
 * @author ChenRui
 * ClassDescription：Welcome KSP Router
 */
object Router {
    internal const val TAG = "Router"


    private val _mailman: Mailman by lazy { Mailman() }


    /**
     * initialize routing related services
     */
    internal fun init(context: Context) {
        _mailman.appInit(context)
    }

    fun with(context: Context): Mailman {
        return _mailman.withContext(context)
    }

    // 参数注入
    fun inject(activity: Activity) {
        _mailman.inject(activity)
    }

}
