package com.hearthappy.router.datahandler

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.hearthappy.router.constant.Constant
import com.hearthappy.router.ext.RouterTypeNames.Activity
import com.hearthappy.router.ext.RouterTypeNames.AppCompatActivity
import com.hearthappy.router.ext.RouterTypeNames.BroadcastReceiver
import com.hearthappy.router.ext.RouterTypeNames.Fragment
import com.hearthappy.router.ext.RouterTypeNames.PathReplaceService
import com.hearthappy.router.ext.RouterTypeNames.SerializationService
import com.hearthappy.router.ext.RouterTypeNames.Service
import com.squareup.kotlinpoet.ksp.toClassName


fun String.reRouterName(): String {
    return this.split("/").joinToString("") { it.replaceFirstChar { rfc -> rfc.uppercaseChar() } }
        .replaceFirstChar { it.uppercase() }
}





// 类型转换逻辑
fun KSClassDeclaration.convertType(): String {
    for (allSuperType in getAllSuperTypes()) {
        val className = allSuperType.toClassName()
        when (className) {
            SerializationService,PathReplaceService -> return Constant.ROUTER_TYPE_SERVICE_PROVIDER
            AppCompatActivity, Activity -> return Constant.ROUTER_TYPE_ACTIVITY
            Service -> return Constant.ROUTER_TYPE_SERVICE
            Fragment -> return Constant.ROUTER_TYPE_FRAGMENT
            BroadcastReceiver -> return Constant.ROUTER_TYPE_BROADCAST
        }

    }
    return "RouteType.UNKNOWN"
}

