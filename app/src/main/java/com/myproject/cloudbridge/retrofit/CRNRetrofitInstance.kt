package com.myproject.cloudbridge.retrofit

import com.myproject.cloudbridge.api.CompanyRegistrationNumberApi
import com.myproject.cloudbridge.util.singleton.MyOkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Company Registration Number
 * 국세청 사업자등록번호 조회 API
 */
object CRNRetrofitInstance {
    private const val BASE_URL = "http://api.odcloud.kr/api/nts-businessman/v1/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(MyOkHttpClient.client)
        .build()

    fun getInstance(): CompanyRegistrationNumberApi = retrofit.create(CompanyRegistrationNumberApi::class.java)
}