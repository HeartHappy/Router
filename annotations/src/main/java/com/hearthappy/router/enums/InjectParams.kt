package com.hearthappy.router.enums

import kotlin.reflect.KClass

data class InjectParams(val name: String, val fieldName: String, val type: KClass<*>)