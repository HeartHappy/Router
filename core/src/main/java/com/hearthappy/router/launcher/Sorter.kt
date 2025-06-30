package com.hearthappy.router.launcher

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.hearthappy.router.analysis.TargetObject
import com.hearthappy.router.core.BuildConfig
import com.hearthappy.router.core.ICourier
import com.hearthappy.router.core.ILogger
import com.hearthappy.router.core.ISorter
import com.hearthappy.router.core.Pack
import com.hearthappy.router.enums.RouteType
import com.hearthappy.router.exception.HandlerException
import com.hearthappy.router.ext.renaming
import com.hearthappy.router.ext.toSmartBundle
import com.hearthappy.router.interfaces.InterceptorCallback
import com.hearthappy.router.interfaces.NavigationCallback
import com.hearthappy.router.logger.DefaultLogger
import com.hearthappy.router.service.ClassLoaderService
import com.hearthappy.router.service.InterceptorService
import com.hearthappy.router.service.PathReplaceService
import com.hearthappy.router.service.SerializationService
import com.hearthappy.router.service.impl.ClassLoaderServiceImpl
import com.hearthappy.router.service.impl.InterceptorServiceImpl
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created Date: 2025/6/26
 * @author ChenRui
 * ClassDescription：
 */
abstract class Sorter : ICourier {
    internal val pack: Pack by lazy { Pack() }

    private val classLoaderService: ClassLoaderService by lazy { ClassLoaderServiceImpl() }
    private val interceptorService: InterceptorService by lazy { InterceptorServiceImpl() }
    internal var serializationService: SerializationService? = null

    private fun initInterceptorService() {
        interceptorService.init(classLoaderService)
    }

    internal fun inject(thiz: Any) {
        classLoaderService.inject(thiz)
    }


    internal fun appInit(context: Context) {
        pack.appContext = context
        initInterceptorService()
    }


    internal fun showLog(isShowLog: Boolean) {
        logger.showLog(isShowLog)
    }

    internal fun setLogger(log: ILogger) {
        logger = log
    }

    private fun setContext(context: Context) {
        pack.context = context
    }

    fun getPath(): String {
        return pack.path
    }

    fun getContext(): Context? {
        return pack.context ?: pack.appContext
    }

    internal fun getExtras(): Bundle {
        return pack.bundle
    }

    fun getOptionsCompat(): Bundle? {
        return pack.optionsCompat
    }


    private fun build(path: String?, uri: Uri?): ICourier {
        path ?: throw HandlerException("Parameter cannot be empty!")
        if (path.isEmpty() || path.isBlank()) throw HandlerException("Parameter is invalid!")
        pack.path = path
        pack.uri = uri
        return this
    }

    fun build(path: String): ICourier {
        val pathReplaceService = Router.getInstance(PathReplaceService::class.java)
        pathReplaceService?.let { build(pathReplaceService.forString(path), null) } ?: build(path, null)
        return this
    }

    fun build(uri: Uri): ICourier {
        val pathReplaceService = Router.getInstance(PathReplaceService::class.java)
        pathReplaceService?.let { build(uri.path, pathReplaceService.forUri(uri)) } ?: build(uri.path, uri)
        return this
    }


    override fun navigation() {
        navigation(null)
    }

    override fun navigation(context: Context?) {
        navigation(context, -1)

    }

    override fun navigation(context: Context?, requestCode: Int) {
        navigation(context, requestCode, null)
    }

    override fun navigation(context: Context?, requestCode: Int, callback: NavigationCallback?) {
        val currentContext = context ?: pack.appContext ?: throw HandlerException("context is null")
        try {
            val targetObject = classLoaderService.getAnnotation(TargetObject::class.java, GENERATE_ROUTER_PATH_PKG.plus(pack.path.renaming()))
            setContext(currentContext)
            val shouldContinue = AtomicBoolean(true)
            callback?.onFound(this)
            val records = interceptorService.getRouterInterceptors()
            if (records.isNotEmpty() && !pack.isGreenChannel()) {
                val isContinue = interceptionHandler(records, shouldContinue, callback)
                if (isContinue) navigationForward(targetObject, currentContext, requestCode, callback)
            } else navigationForward(targetObject, currentContext, requestCode, callback)
            completed()
        } catch (e: ClassNotFoundException) {
            if (BuildConfig.DEBUG) Toast.makeText(currentContext, "No route found for path ['${pack.path}']", Toast.LENGTH_SHORT).show()
            callback?.onLost(this)
        }
    }

    override fun greenChannel(): ISorter {
        pack.greenChannel = true
        return this
    }

    override fun getDestination(): Class<*> {
        val targetObject = classLoaderService.getAnnotation(TargetObject::class.java, GENERATE_ROUTER_PATH_PKG.plus(pack.path.renaming()))
        return targetObject.name.java
    }


    private fun interceptionHandler(records: List<InterceptorServiceImpl.InterceptorRecord>, shouldContinue: AtomicBoolean, callback: NavigationCallback?): Boolean {
        var isContinue = false
        for (record in records) {
            if (!shouldContinue.get()) break
            record.interceptor.intercept(this, object : InterceptorCallback {
                override fun onContinue(director: ICourier) {
                    isContinue = true
                }

                override fun onInterrupt(exception: Throwable?) {
                    callback?.onInterrupt(this@Sorter)
                    exception?.let {
                        logger.debug("onInterrupt: ${exception.message}")
                        shouldContinue.set(false)
                    }
                }
            })
        }
        return isContinue
    }


    private fun navigationForward(targetObject: TargetObject, currentContext: Context, requestCode: Int, callback: NavigationCallback?) {
        when (targetObject.routeType) {
            RouteType.ACTIVITY -> {
                pack.apply {
                    val intent = if (uri == null) Intent(currentContext, targetObject.name.java) else Intent(Intent.ACTION_VIEW, uri).apply { uri?.toSmartBundle(bundle) }
                    intent.run {
                        putExtras(bundle)
                        if (0 != this@apply.flags) setFlags(this@apply.flags)
                        if (currentContext !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (this@apply.action.isNotEmpty()) this.action = this@apply.action
                    }
                    when (currentContext) {
                        is Activity -> ActivityCompat.startActivityForResult(currentContext, intent, requestCode, getOptionsCompat()).also { pendingTransition(currentContext) } //                        else -> currentContext.startActivity(intent, getOptionsCompat())
                        else -> currentContext.startActivity(intent)
                    }


                    callback?.onArrival(this@Sorter)
                }
            }

            RouteType.SERVICE -> {
                val intent = Intent(currentContext, targetObject.name.java)
                intent.apply {
                    putExtras(pack.bundle)
                    if (0 != pack.flags) setFlags(pack.flags)
                    if (pack.action.isNotEmpty()) action = pack.action
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    currentContext.startForegroundService(intent)
                } else {
                    currentContext.startService(intent)
                }
            }
            RouteType.FRAGMENT -> Unit
            RouteType.BROADCAST -> Unit
            RouteType.CONTENT_PROVIDER -> Unit
            RouteType.METHOD -> Unit
            RouteType.SERVICE_PROVIDER -> Unit
            RouteType.UNKNOWN -> Unit
        }

    }

    private fun Pack.pendingTransition(currentContext: Activity) {
        if ((-1 != enterAnim && -1 != exitAnim)) {
            currentContext.overridePendingTransition(enterAnim, exitAnim)
        }
    }

    override fun getInstance(): Any {
        if (pack.path.isEmpty() || pack.path.isBlank()) throw HandlerException("Parameter is invalid!")
        return classLoaderService.getAnnotationClass(TargetObject::class.java, GENERATE_ROUTER_PATH_PKG.plus(pack.path.renaming()), this)
    }

    override fun <T> getInstance(instance: Class<T>): T? {
        return classLoaderService.getInstance(instance)
    }

    private fun completed() {
        pack.clear()
    }


    companion object {
        internal var logger: ILogger = DefaultLogger()

        //生成路径
        internal const val GENERATE_ROUTER_PATH_PKG = "com.hearthappy.router.generate.path."
        internal const val GENERATE_ROUTER_ACTIVITY_PKG = "com.hearthappy.router.generate.routes."
        internal const val GENERATE_ROUTER_INTERCEPTOR_PKG = "com.hearthappy.router.generate.interceptor."
        internal const val GENERATE_ROUTER_PROVIDER_PKG = "com.hearthappy.router.generate.provider."
    }
}