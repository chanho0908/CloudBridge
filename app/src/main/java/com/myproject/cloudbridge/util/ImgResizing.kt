package com.myproject.cloudbridge.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class ImgResizing {
    // 이미지를 리사이즈하는 함수
    fun resizeImage(context: Context, imageResId: Int, targetWidth: Int, targetHeight: Int): Bitmap {
        val options = BitmapFactory.Options().apply {
            // 리소스를 로드하지 않고 원본 이미지의 가로, 세로 크기 정보만 읽어옵니다.
            inJustDecodeBounds = true
            BitmapFactory.decodeResource(context.resources, imageResId, this)

            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, targetWidth, targetHeight)
        }

        // inSampleSize를 적용하여 이미지를 실제로 로드합니다.
        options.inJustDecodeBounds = false
        val resizedBitmap = BitmapFactory.decodeResource(context.resources, imageResId, options)

        // 이미지 크기를 조정하여 반환합니다.
        return Bitmap.createScaledBitmap(resizedBitmap, targetWidth, targetHeight, false)
    }

    // 이미지 크기를 계산하여 샘플 크기를 결정하는 함수
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = (height.toFloat() / reqHeight.toFloat()).toInt()
            val widthRatio = (width.toFloat() / reqWidth.toFloat()).toInt()

            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }
}