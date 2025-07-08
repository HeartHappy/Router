package com.hearthappy.mylibrary2

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


/**
 * Created Date 2019-08-27.
 *
 * @author ChenRui
 * ClassDescription：通知渠道服务类
 */
class NotificationService {

    companion object {
        const val CHANNEL_ID = "app_channel_id_01"
        var START_ID =1

        /**
         * 创建通知渠道
         * NotificationChannel(CHANNEL_ID，渠道名，进程优先级->oom_adj)
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        fun createAllNotificationChannels(context: Context) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "通知消息",
                NotificationManager.IMPORTANCE_DEFAULT
            ) // 设置通知出现时的闪灯（如果 android 设备支持的话）
            notificationChannel.enableLights(true)
            notificationChannel.lightColor =
                Color.RED // 设置通知出现时的震动（如果 android 设备支持的话）,true:震动  false：无震动
            notificationChannel.enableVibration(false) //如果有震动，设置震动效果，默认有震动
            //        notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            //设置声音,默认有声音，传入null无声音
            notificationChannel.setSound(null, null) //最后在notificationmanager中创建该通知渠道
            getNotificationManager(context).createNotificationChannel(notificationChannel)
        }

        /**
         * 获取通知管理器
         *
         * @return
         */
        fun getNotificationManager(context: Context): NotificationManager {
            return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        /**
         * 构建通知
         *
         * @param title      标题
         * @param detailText 详情描述
         * @param jumpClass  点击跳转操作
         * @param context
         * @return 返回构建的通知实例
         */
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN) fun buildNotification(
            context: Context,
            title: String?,
            detailText: String?,
            jumpClass: Class<*>?
        ): Notification { //适配8.0以上
            val builder: NotificationCompat.Builder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 通知渠道的id
                    NotificationCompat.Builder(context, CHANNEL_ID)
                } else { //适配8.0以下
                    NotificationCompat.Builder(context)
                }
            if (jumpClass != null) {
                val intent = Intent(context, jumpClass)
                val pendingIntent = PendingIntent.getActivity(context, 0, intent,if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }) //点击通知跳转操作
                builder.setContentIntent(pendingIntent)
            } //左侧图标
            builder.setSmallIcon(R.mipmap.ic_launcher) //内容标题
            builder.setContentTitle(title) //内容详情
            builder.setContentText(detailText) //设置声音只响一次，不再重复
            builder.setOnlyAlertOnce(true)
//            builder.setVibrate(longArrayOf(0))
            builder.setAutoCancel(true)
//            builder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                builder.setVisibility(Notification.VISIBILITY_PUBLIC)
//            }
            return builder.build()
        }
    }
}