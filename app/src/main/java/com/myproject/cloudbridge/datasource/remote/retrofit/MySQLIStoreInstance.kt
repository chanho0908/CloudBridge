package com.myproject.cloudbridge.datasource.remote.retrofit

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.myproject.cloudbridge.datasource.remote.api.StoreInfoApi
import com.myproject.cloudbridge.datasource.remote.api.StoreMenuApi
import com.myproject.cloudbridge.utility.MyOkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MySQLIStoreInstance {
    val BASE_URL = "http://172.30.1.7/"

    private val gson : Gson = GsonBuilder()
        .setLenient()
        .create()

    private val client: Retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .client(MyOkHttpClient.client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun getStoreApiInstance(): StoreInfoApi = client.create(StoreInfoApi::class.java)
    fun getStoreMenuApiInstance(): StoreMenuApi = client.create(StoreMenuApi::class.java)
}
