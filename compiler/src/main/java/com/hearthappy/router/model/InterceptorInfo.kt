package com.hearthappy.router.model

import com.google.devtools.ksp.symbol.KSFile

class InterceptorInfo {
    var clazz: String = ""
    var priority: Int = 0
    var name: String = ""
    var containingFile: KSFile? = null
    override fun toString(): String {
        return "::clazz='$clazz', priority=$priority, name='$name'"
    }

}