package com.myproject.cloudbridge.model.user

data class User(
    var userKakaoEmail: String ? = "",
    var userID: String ? = "",
    var userPw: String ? = "",
    var userName: String ? = "",
    var userPhone: String ? = ""
)

