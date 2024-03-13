package com.myproject.cloudbridge.model.store

import android.graphics.Bitmap
import com.myproject.cloudbridge.datasource.local.entity.StoreEntity

data class StoreInfoSettingModel (
    var storeInfo: StoreEntity,
    var storeImage: Bitmap
)


