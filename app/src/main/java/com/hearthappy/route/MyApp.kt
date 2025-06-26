package com.hearthappy.route

import android.app.Application
import com.hearthappy.router.core.BuildConfig
import com.hearthappy.router.launcher.Router

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Router.openLog()
    }
}