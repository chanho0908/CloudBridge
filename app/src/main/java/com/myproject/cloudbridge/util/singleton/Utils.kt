package com.myproject.cloudbridge.util.singleton

import android.Manifest
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.util.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Locale

object Utils {

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
    fun getContext(): Context = App.context()

    const val APP_KEY = "c23ff52edb54dc254d59ac484a8d6a2f"
    const val SECRETE_KEY = "t2ivQakqcZ/cvxzekT7Ra9Ja8J1N1lBKu6LqVkijMliEeoD1lLXU0Qei+V9AC8aMbNG+TjVkca70NqFB9akmSg=="
    // 갤러리 권한 요청
    const val ADDR_RESULT = 1002

    fun Base64ToBitmaps(image: String?): Bitmap {
        val encodedByte: ByteArray = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(encodedByte, 0, encodedByte.size)
    }

    fun createRequestBody(text: String): RequestBody {
        return text.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    // 절대경로 변환
    fun absolutelyPath(path: Uri): String? {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = getContext().contentResolver?.query(path, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        val result = index?.let { c.getString(it) }

        c?.close()
        return result
    }

    fun makeStoreMainImage(imgUri: Uri?): MultipartBody.Part {
        // 이미지 절대경로 반환 후 파일 객체 생성
        val file = File(imgUri?.let { absolutelyPath(it) })

        // 파일 객체를 RequestBody Type으로 변환
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

        // MultipartBody.Part Type으로 변환
        return MultipartBody.Part.createFormData("storeMainImage", file.name, requestFile)
    }

    fun accessGallery(launcherForActivity: ActivityResultLauncher<Intent>){
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "image/*"
        )
        launcherForActivity.launch(intent)
    }

    //주소로 위도,경도 구하는 GeoCoding
    fun translateGeo(address: String): Location = try {
        val locations = getContext().let { Geocoder(it, Locale.KOREA).getFromLocationName(address, 1) }

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

    fun String.isNumeric(): Boolean {
        return this.all { it.isDigit() }
    }


    fun showSoftInput(focusView: TextInputEditText) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(100)
            val inputMethodManager = getContext().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(focusView, 0)
        }
    }

    fun hideSoftInput(focusView: TextInputEditText) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(100)
            val inputMethodManager = getContext().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(focusView.windowToken, 0)
        }
    }

    fun requestPlzInputText(msg: String, v: TextInputLayout){
        v.helperText = msg
        v.requestFocus()
    }

}