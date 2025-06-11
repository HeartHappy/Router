package com.hearthappy.router.analysis

import kotlin.reflect.KClass


@Target(AnnotationTarget.CLASS) @Retention(AnnotationRetention.RUNTIME)
annotation class TargetInterceptor(val clazz: KClass<*>, val priority: Int = 0, val name: String = "")