@file:JvmName("PermissionManagementKt")

package com.myproject.cloudbridge.util.management

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.myproject.cloudbridge.util.singleton.Utils.REQUEST_IMAGE_PERMISSIONS
import com.myproject.cloudbridge.util.singleton.Utils.REQUEST_LOCATION_PERMISSIONS
import android.provider.Settings

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}
fun Context.hasImagePermission(): Boolean = REQUEST_IMAGE_PERMISSIONS.any { this.hasPermission(it) }

fun Context.hasLocationPermission(): Boolean = REQUEST_LOCATION_PERMISSIONS.any { this.hasPermission(it) }

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

