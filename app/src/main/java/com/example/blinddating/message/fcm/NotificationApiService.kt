package com.example.blinddating.message.fcm

import com.example.blinddating.message.fcm.Repo.Companion.CONTENT_TYPE
import com.example.blinddating.message.fcm.Repo.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// 어떻게 통신할 것인지.. interface
interface NotificationApiService {
    // 헤더 정보만 요청
    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")

    // Body에 전송할 DTO 객체 생성
    @POST("fcm/send")
    suspend fun postNotification(@Body notification: PushNotification): Response<ResponseBody>
}
