package com.hearthappy.router.model

import com.google.devtools.ksp.symbol.KSFile
import com.hearthappy.router.enums.InjectParams
import com.squareup.kotlinpoet.ClassName
import kotlin.properties.Delegates

class RouterInfo {
    var clazz: String = ""
    var containingFile: KSFile? = null
    val params: MutableList<ParamsInfo> = mutableListOf()
    var routerMeta: RouteMeta by Delegates.notNull()
    var pkg:List<ClassName> = emptyList()
//    var superType: TypeName? = null
}
