package com.hearthappy.route.service

import com.hearthappy.common_api.HelloService
import com.hearthappy.router.annotations.Route

@Route("/service/hello")
class HelloServiceImpl:HelloService {
    override fun sayHello(name: String): String {
        return "Hello $name"
    }
}