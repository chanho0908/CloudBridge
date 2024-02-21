package com.myproject.cloudbridge.model.store

import android.graphics.Bitmap
import com.myproject.cloudbridge.localDB.entity.StoreEntity

data class StoreInfoSettingModel (
    var storeInfo: StoreEntity,
    var storeImage: Bitmap
)


