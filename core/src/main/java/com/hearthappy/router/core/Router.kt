package com.hearthappy.router.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hearthappy.router.annotations.TargetActivity
import com.hearthappy.router.exception.HandlerException
import com.hearthappy.router.exception.NoRouteFoundException
import com.hearthappy.router.ext.rePathName
import com.hearthappy.router.ext.reRouterName

object Router {
    //生成路径
    private const val GENERATE_ROUTER_PATH_PKG = "com.hearthappy.router.generate.path."
    private const val GENERATE_ROUTER_ACTIVITY_PKG = "com.hearthappy.router.generate.act."

    data class ParamInfo(val name: String, val fieldName: String, val type: String)


    // 路由跳转
    fun navigate(context: Context, path: String) {
        navigate(context, path, null)
    }

    fun navigate(context: Context, path: String, params: BundleWrapper?) {
        if (path.isEmpty()) {
            throw HandlerException("Parameter is invalid!")
        }
        val routerClass = Class.forName(GENERATE_ROUTER_PATH_PKG.plus(path.rePathName()))
        val annotation = routerClass.getAnnotation(TargetActivity::class.java) ?: throw NoRouteFoundException("No route found for path ['$path']")
        Log.d(TAG, "navigate:,${annotation.name.java.name},===${annotation.name.simpleName}")
        context.startActivity(Intent(context, Class.forName(annotation.name.java.name)).apply {
            params?.let { putExtras(it.toBundle()) }
        })
    }

    // 参数注入
    fun inject(activity: Activity) {

        val className = activity::class.qualifiedName ?: return //
        val extras = activity.intent.extras ?: return
        val routerPkg = GENERATE_ROUTER_ACTIVITY_PKG.plus(className.reRouterName())
        val forName = Class.forName(routerPkg)
        val newInstance = forName.getDeclaredConstructor().newInstance()

        val declaredField = forName.getDeclaredField("params")
        declaredField.isAccessible = true
        val params = (declaredField.get(newInstance) as List<*>).filterIsInstance<ParamInfo>()
        params.forEach { paramInfo ->
            try {
                val field = activity::class.java.getDeclaredField(paramInfo.fieldName)
                field.isAccessible = true
                val value = extras.get(paramInfo.name) ?: return@forEach
                when (paramInfo.type) {
                    "java.lang.String" -> field.set(activity, value as? String)
                    "int" -> field.setInt(activity, value as? Int ?: 0)
                    "long" -> field.setLong(activity, value as? Long ?: 0L)
                    "boolean" -> field.setBoolean(activity, value as? Boolean ?: false)
                    "float" -> field.setFloat(activity, value as? Float ?: 0f)
                    "double" -> field.setDouble(activity, value as? Double ?: 0.0)
                    else -> field.set(activity, value)
                }
            } catch (e: Exception) {
                Log.e("Router", "Failed to inject field: ${paramInfo.fieldName}", e)
            }
        }
    }


    private const val TAG = "Router"
}
