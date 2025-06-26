package com.hearthappy.router.core

import android.content.Context
import android.net.Uri
import com.hearthappy.router.interfaces.ICourier
import com.hearthappy.router.interfaces.ILogger

/**
 * Created Date: 2025/6/10
 * @author ChenRui
 * ClassDescription：Welcome KSP Router
 */
object Router {
    internal const val TAG = "Router"


    private val _courier by lazy { Courier() }


    /**
     * initialize routing related services
     */
    internal fun init(context: Context) {
        _courier.appInit(context)
    }

    fun <T> getInstance(instance: Class<T>): T? {
        return _courier.getInstance(instance)
    }

    fun openLog() {
        _courier.showLog(true)
    }

    fun setLogger(logger:ILogger){
        _courier.setLogger(logger)
    }

    fun build(path: String): ICourier {
        return _courier.build(path)
    }

    fun build(uri: Uri): ICourier {
        return _courier.build(uri)
    }

    // 参数注入
    fun inject(thiz: Any) {
        _courier.inject(thiz)
    }


}
