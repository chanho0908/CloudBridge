package com.myproject.cloudbridge.model.store

import android.graphics.Bitmap
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class MyStoreMenuRequestModel (
    var imgUrl:  MultipartBody.Part?,
    val crn: RequestBody,
    var productName: RequestBody,
    var productQuantity : RequestBody,
    var productIntro: RequestBody
)