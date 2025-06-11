package com.hearthappy.router.model

import com.google.devtools.ksp.symbol.KSFile
import com.hearthappy.router.enums.ParamInfo

class RouterInfo {
    var clazz: String = ""
    var containingFile: KSFile? = null
    val params: MutableList<ParamInfo> = mutableListOf()
    var supperType: String =""
}
