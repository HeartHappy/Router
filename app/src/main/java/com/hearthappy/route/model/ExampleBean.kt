package com.hearthappy.route.model

import com.hearthappy.router.core.ICourier

data class ExampleBean(val path: String = "", val title: String, var courier: ICourier? = null)

