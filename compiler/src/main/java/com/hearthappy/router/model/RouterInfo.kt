package com.hearthappy.router.model

import com.google.devtools.ksp.symbol.KSFile

class RouterInfo {
    var clazz: String = ""
    var containingFile: KSFile? = null
    val params: MutableList<ParamInfo> = mutableListOf()
}
