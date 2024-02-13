package com.myproject.cloudbridge.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat

import com.google.android.material.snackbar.Snackbar

// 클래스를 생성시
// REQUEST_IMAGE_PERMISSIONS 등등의 상수를 사용하면
// 멤버변수(프로퍼티)가 없게된다.
// object로 변경 -> 객체지향적 관점에서 분명한 코드가된다 -> singleton으로 된다
// Manager 패턴
// 자바에선 괜찮지만 코틀린에서 권장되는 패턴은 아니다.
object PermissionManagement {
    private fun getContext(): Context? = App.context()

    val REQUEST_IMAGE_PERMISSIONS =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

    val REQUEST_LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    fun isPermissionGranted(permission: String): Boolean {
        return getContext()?.let { ContextCompat.checkSelfPermission(it, permission) } == PackageManager.PERMISSION_GRANTED
    }
    fun isImagePermissionGranted(): Boolean =
        REQUEST_IMAGE_PERMISSIONS.any { isPermissionGranted(it) }

    fun isLocationPermissionGranted(): Boolean =
        REQUEST_LOCATION_PERMISSIONS.any { isPermissionGranted(it) }

    fun showPermissionSnackBar(view: View) {
        Snackbar.make(view, "권한이 거부 되었습니다. 설정(앱 정보)에서 권한을 확인해 주세요.",
            Snackbar.LENGTH_SHORT
        ).setAction("확인"){
            //설정 화면으로 이동
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val packageName = getContext()?.packageName
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri

            getContext()?.startActivity(intent)

        }.show()
    }
}