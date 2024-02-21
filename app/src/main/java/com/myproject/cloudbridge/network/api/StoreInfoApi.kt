package com.myproject.cloudbridge.network.api

import com.myproject.cloudbridge.model.store.AllCrnResponseModel
import com.myproject.cloudbridge.model.store.MyStoreInfoResponseModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface StoreInfoApi {

    @GET("/db/storeInfo")
    suspend fun getAllStoreInfo(): List<MyStoreInfoResponseModel>

    @GET("/db/my-store-main-image")
    suspend fun getStoreMainImage(@Query("imagePath") imagePath: String): String

    /**
     * Multipart : 이미지, 오디오, 비디오 등과 같은 여러 종류의 데이터를 서버에 업로드하거나 전송할 때 사용
     * 업로드할 데이터를 @Part 어노테이션을 사용해 파라미터로 지정
     * */
    @Multipart
    @POST("/db/store-registration") // 서버 엔드포인트 URL, HTTP POST 요청
    suspend fun storeRegistration(
        @Part storeMainImage: MultipartBody.Part?,  // 이미지 데이터를 나타내는 파라미터
        @Part("storeName") storeName: RequestBody, // 매장명
        @Part("ceoName") ceoName: RequestBody,     // 점주명
        @Part("crn") crn: RequestBody,             // 사업자 등록 번호
        @Part("contact") contact: RequestBody,     // 전화번호
        @Part("address") address: RequestBody,     // 주소
        @Part("latitude") latitude: RequestBody,   // 위도
        @Part("longitude") longitude: RequestBody, // 경도
        @Part("kind") kind: RequestBody            // 타입
    ): ResponseBody
    // 서버로부터 받은 응답을 처리하기 위한 Retrofit Call 객체를 반환
    // @Part로 나눠진 파라미터를 하나의 객체로 만들어 전송

    @GET("/db/storeInfo/{crn}")
    suspend fun getMyStoreInfo(@Path("crn") crn: String): MyStoreInfoResponseModel

    @GET("/db/all-company-registration-number")
    suspend fun getCompanyRegistrationNumber(): List<AllCrnResponseModel>

    @Multipart
    @PUT("/db/modify-storeInfo")
    suspend fun updateStoreInfo(
        @Part storeMainImage: MultipartBody.Part?,
        @Part("storeName") storeName: RequestBody,
        @Part("ceoName") ceoName: RequestBody,
        @Part("crn") crn: RequestBody,
        @Part("contact") contact: RequestBody,
        @Part("address") address: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("kind") kind: RequestBody,
    ): ResponseBody


    @DELETE("/db/delete-storeInfo/{crn}")
    suspend fun deleteMyStoreInfo(@Path("crn") crn: String): Response<ResponseBody>
}