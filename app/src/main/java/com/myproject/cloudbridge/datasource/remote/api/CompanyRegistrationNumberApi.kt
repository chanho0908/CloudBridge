package com.myproject.cloudbridge.datasource.remote.api

import com.myproject.cloudbridge.model.store.CrnStateRequestModel
import com.myproject.cloudbridge.model.store.CrnStateResponseModel
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface CompanyRegistrationNumberApi {
    @POST("status")
    suspend fun getCRNState(
        @Body requestBody: CrnStateRequestModel,
        @Query("serviceKey") serviceKey:String
    ): CrnStateResponseModel
}