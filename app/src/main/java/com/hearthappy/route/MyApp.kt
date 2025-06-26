package com.hearthappy.route

import android.app.Application
import com.hearthappy.router.launcher.Router

class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        Router.openLog()
    }
}