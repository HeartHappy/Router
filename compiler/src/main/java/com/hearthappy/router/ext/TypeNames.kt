package com.hearthappy.router.ext

import com.hearthappy.router.constant.Constant.APPLICATION
import com.hearthappy.router.constant.Constant.APPLICATION_PKG
import com.hearthappy.router.constant.Constant.CONTEXT
import com.hearthappy.router.constant.Constant.CONTEXT_PKG
import com.squareup.kotlinpoet.ClassName

/**
 * @author ChenRui
 * ClassDescription： 直接创建Classname
 */
object AndroidTypeNames {
    internal val Application = ClassName(APPLICATION_PKG, APPLICATION)
    internal val Context = ClassName(CONTEXT_PKG, CONTEXT)
}

object RouterTypeNames {
    internal val Router = ClassName("com.hearthappy.router.core.Router", "Router")
    internal val RouterParamInfo = ClassName("com.hearthappy.router.enums", "InjectParams")
    internal val TargetActivity=ClassName("com.hearthappy.router.annotations", "TargetActivity")
    internal val AppCompatActivity=ClassName("androidx.appcompat.app",  "AppCompatActivity")
    internal val Activity=ClassName("android.app", "Activity")
    internal val Service=ClassName("android.app", "Service")
    internal val Fragment=ClassName("androidx.fragment.app", "Fragment")
    internal val BroadcastReceiver=ClassName("android.content", "BroadcastReceiver")
    internal val RouteType=ClassName("com.hearthappy.router.enums", "RouteType")
    internal val Interceptor = ClassName(" com.hearthappy.router.generate.interceptor", "Interceptor$$".plus("Group"))
    internal val SerializationService=ClassName("com.hearthappy.router.service", "SerializationService")
    internal val PathReplaceService=ClassName("com.hearthappy.router.service", "PathReplaceService")
    internal val ProviderService=ClassName("com.hearthappy.router.service", "ProviderService")
}

object CollectionsTypeNames{
    internal val List = ClassName("kotlin.collections", "List")
    internal val MutableList = ClassName("kotlin.collections", "MutableList")
    internal val ArrayList = ClassName("kotlin.collections", "ArrayList")
    internal val MutableArrayList = ClassName("kotlin.collections", "MutableArrayList")
    internal val ArrayListOf = ClassName("kotlin.collections", "arrayListOf")
    internal val MutableListOf = ClassName("kotlin.collections", "mutableListOf")

    //map
    internal val Map = ClassName("kotlin.collections", "Map")
    internal val MutableMap = ClassName("kotlin.collections", "MutableMap")
    internal val HashMap = ClassName("kotlin.collections", "HashMap")
    //mapOf
    internal val MapOf = ClassName("kotlin.collections", "mapOf")
    internal val MutableMapOf = ClassName("kotlin.collections", "mutableMapOf")
    internal val HashMapOf = ClassName("kotlin.collections", "hashMapOf")

}
object KotlinTypeNames {
    val UNIT = ClassName("kotlin", "Unit")
    val String = ClassName("kotlin", "String")
    val Int = ClassName("kotlin", "Int")
    val CONTINUATION = ClassName("kotlin.coroutines", "Continuation")
    val COROUTINE_SCOPE = ClassName("kotlinx.coroutines", "CoroutineScope")
    val CHANNEL = ClassName("kotlinx.coroutines.channels", "Channel")
    val RECEIVE_CHANNEL = ClassName("kotlinx.coroutines.channels", "ReceiveChannel")
    val SEND_CHANNEL = ClassName("kotlinx.coroutines.channels", "SendChannel")
    val FLOW = ClassName("kotlinx.coroutines.flow", "Flow")
}
