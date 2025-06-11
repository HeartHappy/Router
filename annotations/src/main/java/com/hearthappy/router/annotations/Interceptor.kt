package com.hearthappy.router.annotations

/**
 * Created Date: 2025/6/10
 * @author ChenRui
 * ClassDescription：Mark a interceptor to interception the route
 *
 * 1、The smaller the priority value, the higher the priority.
 * 2、The priorities are the same and will be executed in order of class name.
 * 3、Call callback.onInterrupt() to interrupt subsequent interceptors
 */
@Retention(AnnotationRetention.RUNTIME) @Target(AnnotationTarget.CLASS)
annotation class Interceptor(val priority: Int, val name: String)
