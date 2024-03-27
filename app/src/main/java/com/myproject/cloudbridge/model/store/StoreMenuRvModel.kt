package com.myproject.cloudbridge.model.store

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoreMenuRvModel (
    var imgBitmap: Bitmap? = null,
    var productName: String,
    var productQuantity : Int,
    var productIntro: String,
) : Parcelable
