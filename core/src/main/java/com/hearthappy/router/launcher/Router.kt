package com.hearthappy.router.launcher

import android.content.Context
import android.net.Uri
import com.hearthappy.router.core.ICourier
import com.hearthappy.router.core.ILogger

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
    @JvmStatic
    fun <T> getInstance(instance: Class<T>): T? {
        return _courier.getInstance(instance)
    }
    @JvmStatic
    fun openLog() {
        _courier.showLog(true)
    }
    @JvmStatic
    fun setLogger(logger: ILogger){
        _courier.setLogger(logger)
    }

    @JvmStatic
    fun build(path: String): ICourier {
        return _courier.build(path)
    }
    @JvmStatic
    fun build(uri: Uri): ICourier {
        return _courier.build(uri)
    }
    @JvmStatic
    fun inject(thiz: Any) {
        _courier.inject(thiz)
    }


}
