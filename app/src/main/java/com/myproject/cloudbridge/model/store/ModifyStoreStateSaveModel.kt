package com.myproject.cloudbridge.model.store

data class ModifyStoreStateSaveModel (
    var storeName: String, // 매장명
    var ceoName: String,   // 점주명
    var contact: String,   // 전화번호
    var address: String,   // 주소
    var kind: String       // 업종
)