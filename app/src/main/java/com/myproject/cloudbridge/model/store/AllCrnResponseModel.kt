package com.myproject.cloudbridge.model.store

import com.google.gson.annotations.SerializedName
// Q. 이렇게 응답 데이터가 하나일 경우 data class를 만드는게 나은가?
//    Map 으로 받는게 나은가 ?
// MySQL에 저장된 모든 사업자 등록 번호를 가져오기 위한 Model Class
// 모든 사업자 등록번호를 가져오기 위한 모델
data class AllCrnResponseModel(
    @SerializedName("crn")
    val companyRegistrationNumber: String
)