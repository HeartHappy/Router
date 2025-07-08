package com.hearthappy.router.service.impl

import androidx.fragment.app.Fragment
import com.hearthappy.router.analysis.TargetObject
import com.hearthappy.router.analysis.TargetServiceProvider
import com.hearthappy.router.exception.HandlerException
import com.hearthappy.router.exception.NoRouteFoundException
import com.hearthappy.router.ext.routeRenaming
import com.hearthappy.router.launcher.Router
import com.hearthappy.router.launcher.Sorter
import com.hearthappy.router.launcher.Sorter.Companion.GENERATE_ROUTER_ACTIVITY_PKG
import com.hearthappy.router.launcher.Sorter.Companion.GENERATE_ROUTER_PROVIDER_PKG
import com.hearthappy.router.service.ClassLoaderService
import com.hearthappy.router.service.PathReplaceService
import com.hearthappy.router.service.SerializationService

class ClassLoaderServiceImpl : ClassLoaderService {


    override fun <A : Annotation> getAnnotation(target : Class<A>, className : String) : A {
        val routerClass = Class.forName(className)
        return routerClass.getAnnotation(target) ?: throw HandlerException("No Annotation found for className ['$className']")
    }

    override fun <A> getAnnotationClass(target : Class<A>, className : String, sorter : Sorter) : Any {
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

    override fun getInstance(className : String) : Any {
        return Class.forName(className).newInstance()
    }



    @Suppress("UNCHECKED_CAST") override fun <T> getInstance(instance : Class<T>) : T? {
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
        } catch (e : ClassNotFoundException) {
            return null
        }
    }

    override fun inject(thiz : Any) {
        val className = thiz::class.qualifiedName ?: return
        try {
            val routerPkg = GENERATE_ROUTER_ACTIVITY_PKG.plus(className.routeRenaming())
            val forName = Class.forName(routerPkg)
            val newInstance = forName.getDeclaredConstructor().newInstance()
            val inject = forName.getMethod("inject", Any::class.java)
            inject.isAccessible=true
            inject.invoke(newInstance, thiz)
        } catch (e : ClassNotFoundException) {
            Router.getRouterEngine().routerLogger.info("No relevant routes found for ['$thiz']")
        }
    }
}