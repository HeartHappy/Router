package com.hearthappy.mylibrary2

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.hearthappy.common_api.RouterPath
import com.hearthappy.mylibrary2.NotificationService.Companion.START_ID
import com.hearthappy.router.annotations.Route

@Route(RouterPath.SERVICE_BACKEND) class RouterService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationService.createAllNotificationChannels(this)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //发送前台通知，适配Android 8.0和以下版本
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForeground(START_ID, NotificationService.buildNotification(this, "Notification Message", "Routing startup service", Modules2Activity::class.java))
        } else {
            startForeground(START_ID, Notification())
        }
        Log.d(TAG, "onStartCommand: ")
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        private const val TAG = "RouterService"
    }
}