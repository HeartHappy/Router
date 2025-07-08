package com.hearthappy.mylibrary2

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.hearthappy.common_api.RouterPath
import com.hearthappy.router.annotations.Route

@Route(RouterPath.SERVICE_BACKEND) class RouterService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //发送前台通知，适配Android 8.0和以下版本
        NotificationUtil.sendNotification(this)
        Log.d(TAG, "onStartCommand: ")
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        private const val TAG = "RouterService"
    }
}