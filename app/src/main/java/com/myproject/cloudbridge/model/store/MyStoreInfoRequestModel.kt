package com.myproject.cloudbridge.model.store
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class MyStoreInfoRequestModel(
    val storeImage: MultipartBody.Part?,
    val storeName: RequestBody,
    val ceoName: RequestBody,
    val crn: RequestBody,
    val contact: RequestBody,
    val address: RequestBody,
    val latitude: RequestBody,
    val longitude: RequestBody,
    val kind: RequestBody
)
