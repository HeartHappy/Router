package com.hearthappy.router.core

import android.app.Activity
import android.util.Log
import com.hearthappy.router.core.Mailman.Companion.GENERATE_ROUTER_ACTIVITY_PKG
import com.hearthappy.router.enums.ParamInfo
import com.hearthappy.router.ext.reRouterName

internal class Courier {

    fun distribution(activity: Activity) {
        val className = activity::class.qualifiedName ?: return
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
}