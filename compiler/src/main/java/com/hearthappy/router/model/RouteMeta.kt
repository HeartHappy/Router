package com.hearthappy.router.model

import com.hearthappy.router.enums.RouteType
import com.squareup.kotlinpoet.ClassName

data class RouteMeta(val routeType:RouteType,val parent:ClassName?=null,val superClass:ClassName?=null)