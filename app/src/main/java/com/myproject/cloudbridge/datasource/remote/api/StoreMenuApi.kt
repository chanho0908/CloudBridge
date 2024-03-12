package com.myproject.cloudbridge.datasource.remote.api

import retrofit2.http.Multipart
import retrofit2.http.POST

interface StoreMenuApi {
    @Multipart
    @POST("/db/store-menu")
    suspend fun storeMenuRegistration()
}