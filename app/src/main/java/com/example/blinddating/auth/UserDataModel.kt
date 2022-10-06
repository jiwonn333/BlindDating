package com.example.blinddating.auth

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserDataModel(
    // null 값 허용 변수 선언
    val email: String? = null,
    val nickname: String? = null,
    val gender: String? = null,
    val city: String? = null,
    val age: String? = null,
    val uid: String? = null

)


