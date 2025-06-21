package com.hearthappy.router.core

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import androidx.core.app.ActivityOptionsCompat
import com.hearthappy.router.analysis.TargetObject
import com.hearthappy.router.enums.RouteType
import com.hearthappy.router.exception.HandlerException
import com.hearthappy.router.exception.NoRouteFoundException
import com.hearthappy.router.ext.rePathName
import com.hearthappy.router.ext.toSmartBundle
import com.hearthappy.router.interfaces.IDirector
import com.hearthappy.router.interfaces.ILogger
import com.hearthappy.router.interfaces.InterceptorCallback
import com.hearthappy.router.logger.DefaultLogger
import com.hearthappy.router.service.ClassLoaderService
import com.hearthappy.router.service.InterceptorService
import com.hearthappy.router.service.PathReplaceService
import com.hearthappy.router.service.SerializationService
import com.hearthappy.router.service.impl.ClassLoaderServiceImpl
import com.hearthappy.router.service.impl.InterceptorServiceImpl
import java.io.Serializable
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created Date: 2025/6/8
 * @author ChenRui
 * ClassDescription：Mailman
 *
 * Responsible for packaging related products and forwarding them to other related personnel.
 * He also provides other services, such as interception.
 */
class Mailman : IDirector {

    private var enterAnim = -1
    private var exitAnim = -1
    private var path: String = ""
    private var uri: Uri? = null
    private var flags: Int = 0
    private var action: String = ""
    private val bundle by lazy { Bundle() }
    private var appContext: Context? = null
    private var context: Context? = null
    private var optionsCompat: Bundle? = null

    private val classLoaderService: ClassLoaderService by lazy { ClassLoaderServiceImpl() }
    private val interceptorService: InterceptorService by lazy { InterceptorServiceImpl() }
    private var serializationService: SerializationService? = null


    fun appInit(context: Context) {
        appContext = context
        initInterceptorService()
    }

    private fun initInterceptorService() {
        interceptorService.init(classLoaderService)
    }


    fun inject(thiz: Any) {
        classLoaderService.inject(thiz)
    }

    fun showLog(isShowLog: Boolean) {
        logger.showLog(isShowLog)
    }

    fun setLogger(log: ILogger) {
        logger = log
    }

    private fun setContext(context: Context): Mailman {
        this.context = context
        return this
    }

    fun getPath(): String {
        return path
    }

    fun getContext(): Context? {
        return context ?: appContext
    }

    fun getExtras(): Bundle {
        return bundle
    }

    fun getOptionsCompat(): Bundle? {
        return optionsCompat
    }


    private fun build(path: String?, uri: Uri?): IDirector {
        path ?: throw HandlerException("Parameter cannot be empty!")
        if (path.isEmpty() || path.isBlank()) throw HandlerException("Parameter is invalid!")
        this.path = path
        this.uri = uri
        return this
    }

    fun build(path: String): IDirector {
        val pathReplaceService = Router.getInstance(PathReplaceService::class.java)
        pathReplaceService?.let { build(pathReplaceService.forString(path), null) } ?: build(path, null)
        return this
    }

    fun build(uri: Uri): IDirector {
        val pathReplaceService = Router.getInstance(PathReplaceService::class.java)
        pathReplaceService?.let { build(uri.path, pathReplaceService.forUri(uri)) } ?: build(uri.path, uri)
        return this
    }

    override fun addFlags(flags: Int): IDirector {
        this.flags = this.flags or flags
        return this
    }

    override fun withAction(action: String): IDirector {
        this.action = action
        return this
    }

    override fun withObject(key: String, value: Any): IDirector {
        serializationService?.let {
            bundle.putString(key, it.toJson(value))
        } ?: run {
            serializationService = Router.getInstance(SerializationService::class.java)
            serializationService?.let { bundle.putString(key, it.toJson(value)) } ?: throw HandlerException("The SerializationService interface has no implementation class")
        }
        return this
    }

    override fun withString(key: String, value: String): IDirector {
        bundle.putString(key, value)
        return this
    }

    override fun withInt(key: String, value: Int): IDirector {
        bundle.putInt(key, value)
        return this
    }

    override fun withBoolean(key: String, value: Boolean): IDirector {
        bundle.putBoolean(key, value)
        return this
    }

    override fun withShort(key: String, value: Short): IDirector {
        bundle.putShort(key, value)
        return this
    }

    override fun withLong(key: String, value: Long): IDirector {
        bundle.putLong(key, value)
        return this
    }

    override fun withFloat(key: String, value: Float): IDirector {
        bundle.putFloat(key, value)
        return this
    }

    override fun withDouble(key: String, value: Double): IDirector {
        bundle.putDouble(key, value)
        return this
    }

    override fun withByte(key: String, value: Byte): IDirector {
        bundle.putByte(key, value)
        return this
    }

    override fun withChar(key: String, value: Char): IDirector {
        bundle.putChar(key, value)
        return this
    }

    override fun withCharSequence(key: String, value: CharSequence): IDirector {
        bundle.putCharSequence(key, value)
        return this
    }

    override fun withParcelable(key: String, value: Parcelable): IDirector {
        bundle.putParcelable(key, value)
        return this
    }

    override fun withParcelableArray(key: String, value: Array<Parcelable>): IDirector {
        bundle.putParcelableArray(key, value)
        return this
    }

    override fun withParcelableArrayList(key: String, value: ArrayList<Parcelable>): IDirector {
        bundle.putParcelableArrayList(key, value)
        return this
    }

    override fun withSparseParcelableArray(key: String, value: SparseArray<out Parcelable>): IDirector {
        bundle.putSparseParcelableArray(key, value)
        return this
    }

    override fun withIntegerArrayList(key: String, value: ArrayList<Int>): IDirector {
        bundle.putIntegerArrayList(key, value)
        return this
    }

    override fun withStringArrayList(key: String, value: ArrayList<String>): IDirector {
        bundle.putStringArrayList(key, value)
        return this
    }

    override fun withCharSequenceArrayList(key: String, value: ArrayList<CharSequence>): IDirector {
        bundle.putCharSequenceArrayList(key, value)
        return this
    }

    override fun withSerializable(key: String, value: Serializable): IDirector {
        bundle.putSerializable(key, value)
        return this
    }

    override fun withByteArray(key: String, value: ByteArray): IDirector {
        bundle.putByteArray(key, value)
        return this
    }

    override fun withShortArray(key: String, value: ShortArray): IDirector {
        bundle.putShortArray(key, value)
        return this
    }

    override fun withCharArray(key: String, value: CharArray): IDirector {
        bundle.putCharArray(key, value)
        return this
    }

    override fun withFloatArray(key: String, value: FloatArray): IDirector {
        bundle.putFloatArray(key, value)
        return this
    }

    override fun withCharSequenceArray(key: String, value: Array<CharSequence>): IDirector {
        bundle.putCharSequenceArray(key, value)
        return this
    }

    override fun withTransition(enterAnim: Int, exitAnim: Int): IDirector {
        this.enterAnim = enterAnim
        this.exitAnim = exitAnim
        return this
    }

    override fun withOptionsCompat(compat: ActivityOptionsCompat): IDirector {
        optionsCompat = compat.toBundle()
        return this
    }

    override fun withBundle(key: String, bundle: Bundle): IDirector {
        this.bundle.putBundle(key, bundle)
        return this
    }


    override fun navigation() {
        navigation(null)
    }

    override fun navigation(context: Context?) {
        val shouldContinue = AtomicBoolean(true)
        val records = interceptorService.getRouterInterceptors()
        if (records.isNotEmpty()) {
            val isContinue = interceptionHandler(records, shouldContinue)
            if (isContinue) navigationForward(context)
        } else navigationForward(context)
        completed()
    }

    private fun interceptionHandler(records: List<InterceptorServiceImpl.InterceptorRecord>, shouldContinue: AtomicBoolean): Boolean {
        var isContinue = false
        for (record in records) {
            if (!shouldContinue.get()) break
            record.interceptor.intercept(this, object : InterceptorCallback {
                override fun onContinue(director: IDirector) {
                    isContinue = true
                }

                override fun onInterrupt(exception: Throwable?) {
                    exception?.let {
                        logger.debug("onInterrupt: ${exception.message}")
                        shouldContinue.set(false)
                    }
                }
            })
        }
        return isContinue
    }


    private fun navigationForward(context: Context?) {
        try {
            val to = classLoaderService.getAnnotation(TargetObject::class.java, GENERATE_ROUTER_PATH_PKG.plus(path.rePathName()))
            val currentContext = context ?: appContext ?: throw HandlerException("context is null")
            setContext(currentContext)
            when (to.routeType) {
                RouteType.ACTIVITY -> {
                    val intent = if (this.uri == null) Intent(currentContext, to.name.java) else Intent(Intent.ACTION_VIEW, this@Mailman.uri).apply { uri?.toSmartBundle(bundle) }
                    intent.apply {
                        putExtras(bundle)
                        if (0 != this@Mailman.flags) setFlags(this@Mailman.flags)
                        if (currentContext !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (this@Mailman.action.isNotEmpty()) action = this@Mailman.action
                    }
                    currentContext.startActivity(intent, getOptionsCompat())
                    when (currentContext) {
                        is Activity -> {
                            if ((-1 != enterAnim && -1 != exitAnim)) {
                                currentContext.overridePendingTransition(enterAnim, exitAnim)
                            }
                        }
                    }
                }

                RouteType.FRAGMENT -> {
                    TODO()
                }

                RouteType.SERVICE -> {
                    val intent = Intent(currentContext, to.name.java)
                    intent.apply {
                        putExtras(bundle)
                        if (0 != this@Mailman.flags) setFlags(this@Mailman.flags)
                        if (this@Mailman.action.isNotEmpty()) action = this@Mailman.action
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        currentContext.startForegroundService(intent);
                    } else {
                        currentContext.startService(intent);
                    }
                }

                RouteType.BROADCAST -> {
                    TODO()
                }

                RouteType.CONTENT_PROVIDER -> {
                    TODO()
                }

                RouteType.METHOD -> {
                    TODO()
                }

                RouteType.SERVICE_PROVIDER -> {
                    TODO()
                }

                RouteType.UNKNOWN -> {
                    TODO()
                }
            }
        } catch (e: ClassNotFoundException) {
            throw NoRouteFoundException("No route found for path ['$path']")
        }
    }

    override fun getInstance(): Any {
        if (path.isEmpty() || path.isBlank()) throw HandlerException("Parameter is invalid!")
        return classLoaderService.getAnnotationClass(TargetObject::class.java, GENERATE_ROUTER_PATH_PKG.plus(path.rePathName()), this)
    }

    override fun <T> getInstance(instance: Class<T>): T? {
        return classLoaderService.getInstance(instance)
    }

    private fun completed() {
        enterAnim = -1
        exitAnim = -1
        path = ""
        flags = 0
        action = ""
        bundle.clear()
        context = null
        uri = null
        optionsCompat?.clear()
        optionsCompat = null
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