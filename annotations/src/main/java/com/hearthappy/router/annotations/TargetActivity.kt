package com.hearthappy.router.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS) @Retention(AnnotationRetention.RUNTIME)
annotation class TargetActivity(val name: KClass<*>,val type:Int=0)