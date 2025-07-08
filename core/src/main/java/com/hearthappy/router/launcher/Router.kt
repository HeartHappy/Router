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


    private val _router by lazy { RouterEngine() }
    internal fun getRouterEngine() = _router

    /**
     * initialize routing related services
     */
    internal fun init(context: Context) {
        _router.init(context)
    }



    @JvmStatic fun <T> getInstance(instance: Class<T>): T? {
        return _router.getInstance(instance)
    }

    @JvmStatic fun openLog() {
        _router.showLog(true)
    }

    @JvmStatic fun setLogger(logger: ILogger) {
        _router.setLogger(logger)
    }

    @JvmStatic fun build(path: String): ICourier {
        return _router.build(path)
    }

    @JvmStatic fun build(uri: Uri): ICourier {
        return _router.build(uri)
    }

    @JvmStatic fun inject(thiz: Any) {
        _router.inject(thiz)
    }


}
