package com.example.blinddating.message.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.blinddating.R
import com.example.blinddating.setting.MyPageActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseService: FirebaseMessagingService() {

    private val TAG = "FirebaseService"
    private val channelId = getString(R.string.channel_id)
    private val name = getString(R.string.channel_name)
    private val descriptionText = getString(R.string.channel_description)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.e(TAG, message.notification?.title.toString())
        Log.e(TAG, message.notification?.body.toString())

        // 앱에서 메시지를 받은 후 앱에서는 notification으로 알림 띄우기
        val title = message.notification?.title.toString()
        val body = message.notification?.body.toString()

        createNotificationChannel()
        showNotification(title, body)
    }

    // notification 채널 등록
    private fun createNotificationChannel() {
        // NotificationChannel 생성하되 api26 이상에서 사용, 라이브러리가 아닌 새 클래스임
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // 시스템에 채널 등록
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, body: String) {
        var notifyId = 123
        val intent = Intent(this, MyPageActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        var builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_dating_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            notify(notifyId, builder.build())
        }
    }
}