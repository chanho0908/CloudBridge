package com.myproject.cloudbridge.model.store
import com.google.gson.annotations.SerializedName

// 사업자 등록 번호 상태 확인을 위한 Model Class
data class CrnStateRequestModel(
    @SerializedName("b_no")
    val bNo: Array<String>
)