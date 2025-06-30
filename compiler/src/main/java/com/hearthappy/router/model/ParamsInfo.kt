package com.hearthappy.router.model

import com.squareup.kotlinpoet.ClassName

data class ParamsInfo(val name: String, val fieldName: String, val type: ClassName,val autowiredType:String,val autowiredParent: ClassName?=null,val isSystemPkg:Boolean=true)
