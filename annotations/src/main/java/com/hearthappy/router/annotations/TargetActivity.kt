package com.hearthappy.router.annotations

@Target(AnnotationTarget.CLASS) @Retention(AnnotationRetention.RUNTIME)
annotation class TargetActivity(val name: String)