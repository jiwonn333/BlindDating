package com.example.blinddating.auth

data class UserDataModel(
    // null 값 허용 변수 선언
    val nickname: String? = null,
    val gender: String? = null,
    val city: String? = null,
    val age: String? = null,
    val uid: String? = null

)

