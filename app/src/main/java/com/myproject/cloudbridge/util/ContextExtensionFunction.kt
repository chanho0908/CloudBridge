package com.myproject.cloudbridge.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.util.singleton.Utils


fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}
fun Context.hasImagePermission(): Boolean = Utils.REQUEST_IMAGE_PERMISSIONS.any { this.hasPermission(it) }

fun Context.hasLocationPermission(): Boolean = Utils.REQUEST_LOCATION_PERMISSIONS.any { this.hasPermission(it) }

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

fun Context.setHelperBoxBlack() = ContextCompat.getColor(this, R.color.helper_box_color_black)
fun Context.setHelperTextRed() = ContextCompat.getColor(this, R.color.helper_text_color_red)
fun Context.setHelperTextRedList() = ContextCompat.getColorStateList(this, R.color.helper_text_color_red)
fun Context.setHelperTextGreen() = ContextCompat.getColor(this, R.color.helper_text_color_green)
fun Context.setHelperTextGreenList() = ContextCompat.getColorStateList(this, R.color.helper_text_color_green)
