package com.myproject.cloudbridge.model.store

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class MyStoreInfoResponseModel(
    val storeName: String,
    val imagePath: String,
    val ceoName: String,
    val crn: String,
    val contact: String,
    val address: String,
    val latitude: String,
    val longitude: String,
    val kind: String,
)
