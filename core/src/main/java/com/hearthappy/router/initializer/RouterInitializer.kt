package com.hearthappy.router.initializer

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.hearthappy.router.core.BuildConfig


/**
 * Created Date: 2025/6/11
 * @author ChenRui
 * ClassDescription：Automatically initialize routing related services
 */
class RouterInitializer : Initializer<ServiceInitializer> {
    override fun create(context: Context): ServiceInitializer {
        if (BuildConfig.DEBUG) Log.d(this::class.java.simpleName, "Router Initializer...")
        return ServiceInitializer(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}