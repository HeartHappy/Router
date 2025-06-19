package com.hearthappy.router.model

import com.google.devtools.ksp.symbol.KSFile
import com.hearthappy.router.enums.ParamInfo
import com.squareup.kotlinpoet.TypeName

class RouterInfo {
    var clazz: String = ""
    var containingFile: KSFile? = null
    val params: MutableList<ParamInfo> = mutableListOf()
    var routerType: String = ""
    var superType: TypeName? = null
}
