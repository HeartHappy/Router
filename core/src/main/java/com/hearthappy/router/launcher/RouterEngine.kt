package com.hearthappy.router.launcher

import android.content.Context
import android.net.Uri
import com.hearthappy.router.core.ICourier
import com.hearthappy.router.core.ILogger
import com.hearthappy.router.exception.HandlerException
import com.hearthappy.router.logger.DefaultLogger
import com.hearthappy.router.service.ClassLoaderService
import com.hearthappy.router.service.InterceptorService
import com.hearthappy.router.service.PathReplaceService
import com.hearthappy.router.service.impl.ClassLoaderServiceImpl
import com.hearthappy.router.service.impl.InterceptorServiceImpl

internal class RouterEngine {

    internal val classLoaderService: ClassLoaderService by lazy { ClassLoaderServiceImpl() }
    internal val interceptorService: InterceptorService by lazy { InterceptorServiceImpl() }
    internal var appContext: Context? = null
    internal var routerLogger: ILogger = DefaultLogger()
    private fun initInterceptorService() {
        interceptorService.init(classLoaderService)
    }

    internal fun inject(thiz: Any) {
        classLoaderService.inject(thiz)
    }

    fun init(context: Context) {
        appContext = context
        initInterceptorService()
    }

    internal fun showLog(isShowLog: Boolean) {
        routerLogger.showLog(isShowLog)
    }

    internal fun setLogger(log: ILogger) {
        routerLogger = log
    }
    fun <T> getInstance(instance: Class<T>): T? {
        return classLoaderService.getInstance(instance)
    }

    private fun build(path: String?, uri: Uri?): ICourier {
        path ?: throw HandlerException("Parameter cannot be empty!")
        if (path.isEmpty() || path.isBlank()) throw HandlerException("Parameter is invalid!")
        return Courier(path, uri)
    }

    fun build(path: String): ICourier {
        val pathReplaceService = Router.getInstance(PathReplaceService::class.java)
        return pathReplaceService?.run { build(pathReplaceService.forString(path), null) } ?: build(path, null)
    }

    fun build(uri: Uri): ICourier {
        val pathReplaceService = Router.getInstance(PathReplaceService::class.java)
        return pathReplaceService?.run { build(uri.path, pathReplaceService.forUri(uri)) } ?: build(uri.path, uri)
    }

}