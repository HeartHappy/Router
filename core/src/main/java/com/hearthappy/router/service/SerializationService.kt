package com.hearthappy.router.service

import java.lang.reflect.Type

interface SerializationService {
    /**
     * Object to json
     *
     * @param instance obj
     * @return json string
     */
    fun toJson(instance: Any?): String?

    /**
     * Parse json to object
     *
     * @param input json string
     * @param clazz object type
     * @return instance of object
     */
    fun <T> fromJson(input: String?, clazz: Type?): T
}