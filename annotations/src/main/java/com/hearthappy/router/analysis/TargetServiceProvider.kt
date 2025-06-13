package com.hearthappy.router.analysis

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS) @Retention(AnnotationRetention.RUNTIME)
annotation class TargetServiceProvider(val clazz: KClass<*>)
