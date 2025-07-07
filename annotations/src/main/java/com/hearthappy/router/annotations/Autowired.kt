package com.hearthappy.router.annotations

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Autowired(
    val name: String = ""
)