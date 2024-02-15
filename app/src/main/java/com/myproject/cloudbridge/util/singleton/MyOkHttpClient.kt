package com.myproject.cloudbridge.util.singleton
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object MyOkHttpClient {
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor())
            .connectTimeout(100, TimeUnit.MINUTES)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .build()
    }

    private fun httpLoggingInterceptor(): Interceptor{
        return HttpLoggingInterceptor { message -> Log.e("MyHTTPLog :", message + "") }
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }

}