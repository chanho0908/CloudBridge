package com.myproject.cloudbridge.model.store

import android.graphics.Bitmap
import com.myproject.cloudbridge.db.entity.StoreEntity

data class MyStoreInfoSettingModel (
    var storeInfo: StoreEntity,
    var storeImage: Bitmap
)


