package com.hearthappy.router.core

import android.content.Context
import android.net.Uri
import android.os.Bundle
import com.hearthappy.router.exception.HandlerException

class Pack {
    internal var enterAnim = -1
    internal var exitAnim = -1
    internal var path: String = ""
    internal var uri: Uri? = null
    internal var flags: Int = 0
    internal var action: String = ""
    internal val bundle by lazy { Bundle() }
    internal var appContext: Context? = null
    internal var context: Context? = null
    internal var optionsCompat: Bundle? = null

    fun getContext(): Context {
        return context?: appContext?: throw HandlerException("context is null")
    }

    fun clear(){
        enterAnim = -1
        exitAnim = -1
        path = ""
        flags = 0
        action = ""
        bundle.clear()
        context = null
        uri = null
        optionsCompat?.clear()
        optionsCompat = null
    }
}