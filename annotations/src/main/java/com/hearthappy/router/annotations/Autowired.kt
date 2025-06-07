package com.hearthappy.router.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Autowired(
    val name: String = ""
)