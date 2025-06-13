package com.hearthappy.route.service

import com.google.gson.Gson
import com.hearthappy.route.RouterPath
import com.hearthappy.router.annotations.Route
import com.hearthappy.router.service.SerializationService
import java.lang.reflect.Type

@Route(RouterPath.SERVICE_JSON)
class JsonService:SerializationService {
    private val gson=Gson()
    override fun toJson(instance: Any?): String? {
       return gson.toJson(instance)
    }

    override fun <T> fromJson(input: String?, clazz: Type?): T {
       return gson.fromJson(input,clazz)
    }
}