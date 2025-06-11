package com.hearthappy.router.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.util.SparseArray
import com.hearthappy.router.analysis.TargetObject
import com.hearthappy.router.enums.RouteType
import com.hearthappy.router.exception.HandlerException
import com.hearthappy.router.exception.NoRouteFoundException
import com.hearthappy.router.ext.rePathName
import com.hearthappy.router.interfaces.IDirector
import com.hearthappy.router.interfaces.InterceptorCallback
import com.hearthappy.router.service.InterceptorService
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
    private var flags: Int = 0
    private var action: String = ""
    private val bundle by lazy { Bundle() }
    private var context: Context? = null

    private val _courier: Courier by lazy { Courier() }
    private val interceptorService: InterceptorService by lazy { InterceptorServiceImpl() }


    fun initInterceptorService() {
        interceptorService.init()
    }

    fun inject(activity: Activity) {
        _courier.distribution(activity)
    }


    internal fun withContext(context: Context): Mailman {
        this.context = context
        return this
    }

    fun getPath(): String {
        return path
    }

    fun getContext(): Context? {
        return context
    }


    fun build(path: String): IDirector {
        if (path.isEmpty() || path.isBlank()) throw HandlerException("Parameter is invalid!")
        this.path = path
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
        TODO("Not yet implemented")
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

    override fun withBundle(key: String, bundle: Bundle): IDirector {
        this.bundle.putBundle(key, bundle)
        return this
    }

    override fun navigation() {
        val shouldContinue = AtomicBoolean(true)
        for (record in interceptorService.getRouterInterceptors()) {
            if (!shouldContinue.get()) break
            record.interceptor.intercept(this, object : InterceptorCallback {
                override fun onContinue(director: IDirector) {
                    _navigation()
                }

                override fun onInterrupt(exception: Throwable?) {
                    if (exception != null) {
                        if (BuildConfig.DEBUG) Log.e(Router.TAG, "onInterrupt: ${exception?.message}")
                        shouldContinue.set(false)
                    }
                }
            })
        }
        completed()
    }

    private fun _navigation() {

        try {
            val routerClass = Class.forName(GENERATE_ROUTER_PATH_PKG.plus(path.rePathName()))
            val tb = routerClass.getAnnotation(TargetObject::class.java) ?: throw NoRouteFoundException("No route found for path ['$path']")
            val currentContext = context ?: throw HandlerException("context is null")
            when (tb.routeType) {
                RouteType.ACTIVITY -> {
                    val intent = Intent(currentContext, tb.name.java).apply {
                        putExtras(bundle)
                        if (0 != this@Mailman.flags) setFlags(this@Mailman.flags)
                        if (currentContext !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (this@Mailman.action.isNotEmpty()) action = this@Mailman.action
                    }
                    currentContext.startActivity(intent)
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
                    TODO()
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
                RouteType.UNKNOWN -> {
                    TODO()
                }
            }
        } catch (e: ClassNotFoundException) {
            throw NoRouteFoundException("No route found for path ['$path']")
        }
    }


    private fun completed() {
        enterAnim = -1
        exitAnim = -1
        path = ""
        flags = 0
        action = ""
        bundle.clear()
    }


    companion object {
        //生成路径
        private const val GENERATE_ROUTER_PATH_PKG = "com.hearthappy.router.generate.path."
        internal const val GENERATE_ROUTER_ACTIVITY_PKG = "com.hearthappy.router.generate.routes."
        internal const val GENERATE_ROUTER_INTERCEPTOR_PKG = "com.hearthappy.router.generate.interceptor."
    }

}