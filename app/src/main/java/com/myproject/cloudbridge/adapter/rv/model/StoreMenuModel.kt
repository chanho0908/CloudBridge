package com.myproject.cloudbridge.adapter.rv.model

import android.graphics.Bitmap

data class StoreMenuModel (
    var imgBitmap: Bitmap? = null,
    var productName: String,
    var productQuantity : Int,
    var productIntro: String,
)