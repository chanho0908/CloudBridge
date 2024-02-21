package com.myproject.cloudbridge.localDB.typeConverter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class ImageTypeConverter {
    // Server -> App -> Room
    // Bitmap -> ByteArray
    @TypeConverter
    fun fromBitmapToByteArray(bitmap: Bitmap) : ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    // Room -> App
    @TypeConverter
    fun fromByteArrayToBitmap(byteArray: ByteArray) : Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}