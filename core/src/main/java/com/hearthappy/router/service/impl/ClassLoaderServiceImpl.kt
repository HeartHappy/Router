package com.hearthappy.router.service.impl

import android.app.Activity
import android.app.Service
import android.util.Log
import androidx.fragment.app.Fragment
import com.hearthappy.router.analysis.TargetObject
import com.hearthappy.router.analysis.TargetServiceProvider
import com.hearthappy.router.core.Mailman
import com.hearthappy.router.core.Mailman.Companion.GENERATE_ROUTER_ACTIVITY_PKG
import com.hearthappy.router.core.Mailman.Companion.GENERATE_ROUTER_PROVIDER_PKG
import com.hearthappy.router.core.Router
import com.hearthappy.router.enums.ParamInfo
import com.hearthappy.router.exception.HandlerException
import com.hearthappy.router.exception.NoRouteFoundException
import com.hearthappy.router.ext.reRouterName
import com.hearthappy.router.service.ClassLoaderService
import com.hearthappy.router.service.PathReplaceService
import com.hearthappy.router.service.SerializationService

class ClassLoaderServiceImpl : ClassLoaderService {


    override fun <A : Annotation> getAnnotation(target: Class<A>, className: String): A {
        val routerClass = Class.forName(className)
        return routerClass.getAnnotation(target) ?: throw HandlerException("No Annotation found for className ['$className']")
    }

    override fun <A> getAnnotationClass(target: Class<A>, className: String, mailman: Mailman): Any {
        val routerClass = Class.forName(className)
        val tb = routerClass.getAnnotation(TargetObject::class.java) ?: throw NoRouteFoundException("No instance found for className ['$className']")
        val instance = tb.name.java.newInstance() //TODO Fragment初始化，支持androidx和supper.v4
        if (instance is Fragment) {
            instance.arguments = mailman.getExtras()
        } //        else if (instance is android.support.v4.app.Fragment) {
        //            (instance as android.support.v4.app.Fragment).setArguments(mailman.getExtras())
        //        }
        return instance
    }

    override fun getInstance(className: String): Any {
        return Class.forName(className).newInstance()
    }


    @Suppress("UNCHECKED_CAST") override fun <T> getInstance(instance: Class<T>): T? {
        try {
            when (instance) {
                SerializationService::class.java -> {
                    val serviceProvider = getAnnotation(TargetServiceProvider::class.java, GENERATE_ROUTER_PROVIDER_PKG.plus("Provider$$").plus("Json"))
                    return serviceProvider.clazz.java.newInstance() as T
                }
                PathReplaceService::class.java -> {
                    val serviceProvider = getAnnotation(TargetServiceProvider::class.java, GENERATE_ROUTER_PROVIDER_PKG.plus("Provider$$").plus("PathReplace"))
                    return serviceProvider.clazz.java.newInstance() as T
                }
                else -> {}
            }
        } catch (e: ClassNotFoundException) {
            return null
        }
        return null
    }

    override fun inject(thiz: Any) {
        val className = thiz::class.qualifiedName ?: return
        val extras = when (thiz) {
            is Fragment -> thiz.arguments
            is Activity -> thiz.intent.extras
            else -> null
        } //No parameters are carried
        if (extras?.isEmpty == true) return
        val routerPkg = GENERATE_ROUTER_ACTIVITY_PKG.plus(className.reRouterName())
        val forName = Class.forName(routerPkg)
        val newInstance = forName.getDeclaredConstructor().newInstance()

        val declaredField = forName.getDeclaredField("params")
        declaredField.isAccessible = true
        val params = (declaredField.get(newInstance) as List<*>).filterIsInstance<ParamInfo>() //The target object needs to inject a parameter list
        params.forEach { paramInfo ->
            try {
                val field = thiz::class.java.getDeclaredField(paramInfo.fieldName)
                field.isAccessible = true
                val value = extras?.get(paramInfo.name) ?: return@forEach
                when (paramInfo.type) {
                    "kotlin.String" -> field.set(thiz, value as? String)
                    "java.lang.String" -> field.set(thiz, value as? String)
                    "int" -> field.setInt(thiz, value as? Int ?: 0)
                    "long" -> field.setLong(thiz, value as? Long ?: 0L)
                    "boolean" -> field.setBoolean(thiz, value as? Boolean ?: false)
                    "float" -> field.setFloat(thiz, value as? Float ?: 0f)
                    "double" -> field.setDouble(thiz, value as? Double ?: 0.0)
                    else -> { // 处理复杂对象类型
                        if (value is String) {
                            try { // 尝试将字符串解析为JSON对象
                                val clazz = Class.forName(paramInfo.type)
                                val serializationService = getInstance(SerializationService::class.java) // 假设您使用了JSON解析库，如Gson或FastJSON
                                serializationService?.fromJson<Any>(value, clazz).run { field.set(thiz, this) }
                            } catch (e: Exception) {
                                Log.e(Router.TAG, "Failed to parse JSON for field: ${paramInfo.fieldName}", e) // 如果解析失败，尝试直接设置原始值
                                field.set(thiz, value)
                            }
                        } else { // 对于其他类型，尝试直接设置
                            field.set(thiz, value)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(Router.TAG, "Failed to inject field: ${paramInfo.fieldName}", e)
            }
        }
    }
}