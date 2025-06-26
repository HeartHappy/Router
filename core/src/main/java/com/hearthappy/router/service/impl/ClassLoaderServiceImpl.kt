package com.hearthappy.router.service.impl

import android.app.Activity
import android.os.Parcelable
import android.util.SparseArray
import androidx.fragment.app.Fragment
import com.hearthappy.router.analysis.TargetObject
import com.hearthappy.router.analysis.TargetServiceProvider
import com.hearthappy.router.launcher.Router
import com.hearthappy.router.launcher.Sorter
import com.hearthappy.router.launcher.Sorter.Companion.GENERATE_ROUTER_ACTIVITY_PKG
import com.hearthappy.router.launcher.Sorter.Companion.GENERATE_ROUTER_PROVIDER_PKG
import com.hearthappy.router.launcher.Sorter.Companion.logger
import com.hearthappy.router.enums.InjectParams
import com.hearthappy.router.exception.HandlerException
import com.hearthappy.router.exception.NoRouteFoundException
import com.hearthappy.router.ext.routeRenaming
import com.hearthappy.router.service.ClassLoaderService
import com.hearthappy.router.service.PathReplaceService
import com.hearthappy.router.service.ProviderService
import com.hearthappy.router.service.SerializationService
import java.lang.reflect.Field

class ClassLoaderServiceImpl : ClassLoaderService {


    override fun <A : Annotation> getAnnotation(target: Class<A>, className: String): A {
        val routerClass = Class.forName(className)
        return routerClass.getAnnotation(target) ?: throw HandlerException("No Annotation found for className ['$className']")
    }

    override fun <A> getAnnotationClass(target: Class<A>, className: String, sorter: Sorter): Any {
        val routerClass = Class.forName(className)
        val tb = routerClass.getAnnotation(TargetObject::class.java) ?: throw NoRouteFoundException("No instance found for className ['$className']")
        val instance = tb.name.java.newInstance() //TODO Fragment初始化，支持androidx和supper.v4
        if (instance is Fragment) {
            instance.arguments = sorter.getExtras()
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
                else -> {
                    val serviceProvider = getAnnotation(TargetServiceProvider::class.java, GENERATE_ROUTER_PROVIDER_PKG.plus("Provider$$").plus(instance.simpleName))
                    return serviceProvider.clazz.java.newInstance() as T
                }
            }
        } catch (e: ClassNotFoundException) {
            return null
        }
    }

    override fun inject(thiz: Any) {
        val className = thiz::class.qualifiedName ?: return
      //No parameters are carried //        if (extras?.isEmpty == true) return
        val routerPkg = GENERATE_ROUTER_ACTIVITY_PKG.plus(className.routeRenaming())
        val forName = Class.forName(routerPkg)
        val newInstance = forName.getDeclaredConstructor().newInstance()

        val declaredField = forName.getDeclaredField("params")
        declaredField.isAccessible = true
        val params = (declaredField.get(newInstance) as List<*>).filterIsInstance<InjectParams>() //The target object needs to inject a parameter list
        val extras = when (thiz) {
            is Fragment -> thiz.arguments
            is Activity -> thiz.intent.extras
            else -> null
        }
        params.forEach { paramInfo ->
            try {
                val field = thiz::class.java.getDeclaredField(paramInfo.fieldName)
                field.isAccessible = true //传递的参数
                val value = extras?.get(paramInfo.name)
                value?.let { result ->
                    when (paramInfo.type) {
                        String::class -> field.set(thiz, result as? String)
                        Integer::class -> field.set(thiz, result as? Int)
                        Short::class -> field.set(thiz, result as? Short)
                        Long::class -> field.set(thiz, result as? Long)
                        Byte::class -> field.set(thiz, result as? Byte)
                        Character::class -> field.set(thiz, result as? Char)
                        Float::class -> field.set(thiz, result as? Float)
                        Double::class -> field.set(thiz, result as? Double)
                        Char::class -> field.set(thiz, result as? Char)
                        Parcelable::class -> field.set(thiz, result as? Parcelable)
                        ArrayList::class -> field.set(thiz, result as? ArrayList<*>)
                        SparseArray::class -> field.set(thiz, result as? SparseArray<*>)
                        Boolean::class -> field.set(thiz, result as? Boolean)
                        else -> field.setObject(result, paramInfo, thiz) // 处理复杂对象类型
                    }
                } ?: let { //该注入参数没有传递
                    if (paramInfo.type.java.isInterface && ProviderService::class.java.isAssignableFrom(paramInfo.type.java)) { //注入服务
                        logger.debug("inject: ProviderService:${paramInfo.type.java.simpleName}")
                        field.set(thiz, Router.getInstance(paramInfo.type.java))
                    } else {
                        logger.debug("inject: No parameters were passed in :$paramInfo")
                    }
                }

            } catch (e: Exception) {
                logger.error(Router.TAG, "Failed to inject field: ${paramInfo.fieldName}", e)
            }
        }
    }


    private fun Field.setObject(result: Any, injectParams: InjectParams, thiz: Any) {
        val clazz = injectParams.type.qualifiedName?.let { Class.forName(it) }
        val serializationService = getInstance(SerializationService::class.java) // 假设您使用了JSON解析库，如Gson或FastJSON
        serializationService?.fromJson<Any>(result as? String, clazz).run { set(thiz, this) }
    }
}