package com.hearthappy.router.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Route(
    val path: String,
    val name: String = ""
)