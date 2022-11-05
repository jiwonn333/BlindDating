package com.example.blinddating.message.fcm

import com.example.blinddating.message.fcm.Repo.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager {
    companion object {

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val service = retrofit.create(NotificationApiService::class.java)
    }
}