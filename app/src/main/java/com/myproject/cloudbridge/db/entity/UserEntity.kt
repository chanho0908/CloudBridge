package com.myproject.cloudbridge.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity (
    @PrimaryKey
    val userKakaoEmail : String,
    val userID : String,
    val userPw: String,
    val userName: String,
    val userPhone: String
)