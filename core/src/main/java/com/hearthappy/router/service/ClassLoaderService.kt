package com.hearthappy.router.service

import com.hearthappy.router.core.Mailman

/**
 * Created Date: 2025/6/14
 * @author ChenRui
 * ClassDescription： Class loading service
 */
interface ClassLoaderService {


    fun <A : Annotation> getAnnotation(target : Class<A>, className : String) : A

    fun <A> getAnnotationClass(target : Class<A>, className : String, mailman : Mailman) : Any

    fun getInstance(className : String) : Any

    fun <T> getInstance(instance : Class<T>) : T?

    fun inject(thiz  : Any)
}