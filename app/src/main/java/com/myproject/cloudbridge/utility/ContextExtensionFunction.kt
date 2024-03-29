package com.myproject.cloudbridge.utility

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.SystemClock
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.myproject.cloudbridge.R
import java.util.Locale
import kotlin.concurrent.thread


fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}
fun Context.hasImagePermission(): Boolean = Utils.REQUEST_IMAGE_PERMISSIONS.any { this.hasPermission(it) }

fun Context.hasLocationPermission(): Boolean = Utils.REQUEST_LOCATION_PERMISSIONS.any { this.hasPermission(it) }

// 입력 요소가 비어있을때 보여줄 다이얼로그를 구성하는 메서드
fun Context.showErrorDialog(view: View, title:String, message:String){
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
    materialAlertDialogBuilder.setTitle(title)
    materialAlertDialogBuilder.setMessage(message)
    materialAlertDialogBuilder.setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
        Utils.showSoftInput(view)
    }
    materialAlertDialogBuilder.show()
}

fun Context.showPermissionSnackBar(view: View) {
    Snackbar.make(view, "권한이 거부 되었습니다. 설정(앱 정보)에서 권한을 확인해 주세요.",
        Snackbar.LENGTH_SHORT
    ).setAction("확인"){
        //설정 화면으로 이동
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val packageName = this.packageName
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri

        this.startActivity(intent)

    }.show()
}

//주소로 위도,경도 구하는 GeoCoding
fun Context.translateGeo(address: String): Location = try {
    val locations = Geocoder(this, Locale.KOREA).getFromLocationName(address, 1)

    if (!locations.isNullOrEmpty()) {
        Location("").apply {
            latitude = locations[0].latitude
            longitude = locations[0].longitude
        }
    } else {
        throw Exception("주소를 변환할 수 없습니다.")
    }
} catch (e: Exception) {
    e.printStackTrace()
    // 예외 발생 시 빈 Location 객체를 반환
    Location("").apply {
        latitude = 0.0
        longitude = 0.0
    }
}

fun Context.showSoftInput(focusView: TextInputEditText) {
    val inputMethodManager = this@showSoftInput.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE)
            as InputMethodManager
    inputMethodManager.showSoftInput(focusView, 0)

}

fun Context.hideSoftInput(focusView: TextInputEditText) {
    val inputMethodManager = this@hideSoftInput.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE)
            as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(focusView.windowToken, 0)

}

// 이미지를 리사이즈하는 함수
fun Context.resizeImage(imageResId: Int, targetWidth: Int, targetHeight: Int): Bitmap {
    val options = BitmapFactory.Options().apply {
        // 리소스를 로드하지 않고 원본 이미지의 가로, 세로 크기 정보만 읽어옵니다.
        inJustDecodeBounds = true
        BitmapFactory.decodeResource(this@resizeImage.resources, imageResId, this)

        // Calculate inSampleSize
        inSampleSize = calculateInSampleSize(this, targetWidth, targetHeight)
    }

    // inSampleSize를 적용하여 이미지를 실제로 로드합니다.
    options.inJustDecodeBounds = false
    val resizedBitmap = BitmapFactory.decodeResource(this@resizeImage.resources, imageResId, options)

    // 이미지 크기를 조정하여 반환합니다.
    return Bitmap.createScaledBitmap(resizedBitmap, targetWidth, targetHeight, false)
}

fun Context.setHelperBoxBlack() = ContextCompat.getColor(this, R.color.helper_box_color_black)
fun Context.setHelperTextRed() = ContextCompat.getColor(this, R.color.helper_text_color_red)
fun Context.setHelperTextRedList() = ContextCompat.getColorStateList(this, R.color.helper_text_color_red)
fun Context.setHelperTextGreen() = ContextCompat.getColor(this, R.color.helper_text_color_green)
fun Context.setHelperTextGreenList() = ContextCompat.getColorStateList(this, R.color.helper_text_color_green)

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
