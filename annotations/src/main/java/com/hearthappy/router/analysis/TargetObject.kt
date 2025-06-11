package com.hearthappy.router.analysis

import com.hearthappy.router.enums.RouteType
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS) @Retention(AnnotationRetention.RUNTIME)
annotation class TargetObject(val name: KClass<*>, val routeType: RouteType = RouteType.UNKNOWN)