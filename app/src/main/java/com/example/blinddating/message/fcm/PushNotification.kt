package com.example.blinddating.message.fcm

// 어떤 데이터를 보낼 것인지
class PushNotification(
    val data: NotificationModel,
    val token: String
)