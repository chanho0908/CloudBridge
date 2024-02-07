package com.myproject.cloudbridge.model.user

import com.myproject.cloudbridge.model.store.MyStoreInfoResponseModel

data class UserSelectStoreModel (
    val store: MyStoreInfoResponseModel,
    val select: Boolean
)