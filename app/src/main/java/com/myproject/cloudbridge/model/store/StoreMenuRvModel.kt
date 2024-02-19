package com.myproject.cloudbridge.model.store

import android.graphics.Bitmap

data class StoreMenuRvModel (
    var imgBitmap: Bitmap? = null,
    var productName: String,
    var productQuantity : Int,
    var productIntro: String,
)