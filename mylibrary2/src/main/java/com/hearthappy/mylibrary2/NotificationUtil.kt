package com.hearthappy.mylibrary2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationUtil {
    private const val CHANNEL_ID = "default_channel"
    private const val CHANNEL_NAME = "默认通知"
    private const val NOTIFICATION_ID = 1001

    /**
     * 发送通知（适配所有机型和Android版本）
     *
     * @param context   上下文
     * @param title     通知标题
     * @param content   通知内容
     * @param smallIcon 小图标资源ID（必须使用白色透明底图标）
     * @param largeIcon 大图标资源ID（可选，传0则使用应用图标）
     */
    fun sendNotification(context: Context, title: String?, content: String?, smallIcon: Int, largeIcon: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        // 3. 创建通知渠道（Android 8.0+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "重要通知通道"
            manager.createNotificationChannel(channel)
        }

        // 4. 构建通知（适配不同版本）
        val builder = NotificationCompat.Builder(context, CHANNEL_ID).setContentTitle(title).setContentText(content).setSmallIcon(smallIcon) // 必须使用白色透明底图标
            .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // 设置大图标（可选）
        if (largeIcon != 0) {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, largeIcon))
        } else { // 默认使用应用图标
            builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, context.applicationInfo.icon))
        }

        // 5. 发送通知
        manager.notify(NOTIFICATION_ID, builder.build())
    }


    // 示例调用方法
    fun sendNotification(context: Context) {
        sendNotification(context, "重要更新通知", "新版本已发布，请立即更新体验最新功能！", R.mipmap.ic_launcher,  R.mipmap.ic_launcher  )
    }
}
