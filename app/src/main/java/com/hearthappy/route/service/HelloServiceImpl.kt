package com.hearthappy.route.service

import com.hearthappy.common_api.HelloService
import com.hearthappy.common_api.RouterPath
import com.hearthappy.router.annotations.Route

@Route(RouterPath.SERVICE_HELLO)
class HelloServiceImpl:HelloService {
    override fun sayHello(name: String): String {

        return "Hello $name"
    }
}