package com.hearthappy.router.initializer

import android.content.Context
import com.hearthappy.router.launcher.Router

class ServiceInitializer(context: Context) {
    init {
        Router.init(context)
    }
}