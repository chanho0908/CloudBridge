package com.myproject.cloudbridge.model.store

data class CrnStateResponseModel (
    val request_cnt: Int,
    val match_cnt: Int,
    val status_code: String,
    val data: List<CompanyRegistrationNumberState>
)