package com.hearthappy.common_api

import com.hearthappy.router.service.ProviderService


interface HelloService: ProviderService {

    fun sayHello(name: String): String
}