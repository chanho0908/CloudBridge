package com.myproject.cloudbridge.model.store

data class ModifyStoreStateSaveModel (
    var storeName: String,
    var ceoName: String,
    var contact: String,
    var address: String,
    var kind: String
)