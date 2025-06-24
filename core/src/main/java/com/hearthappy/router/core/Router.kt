package com.hearthappy.router.core

import android.content.Context
import android.net.Uri
import com.hearthappy.router.interfaces.IDirector
import com.hearthappy.router.interfaces.ILogger

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

    fun <T> getInstance(instance: Class<T>): T? {
        return _mailman.getInstance(instance)
    }

    fun openLog() {
        _mailman.showLog(true)
    }

    fun setLogger(logger:ILogger){
        _mailman.setLogger(logger)
    }

    fun build(path: String): IDirector {
        return _mailman.build(path)
    }

    fun build(uri: Uri): IDirector {
        return _mailman.build(uri)
    }

    // 参数注入
    fun inject(thiz: Any) {
        _mailman.inject(thiz)
    }


}
