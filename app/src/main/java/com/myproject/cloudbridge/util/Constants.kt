package com.myproject.cloudbridge.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.myproject.cloudbridge.R
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

class Constants {
    companion object{
        const val APP_KEY = "c23ff52edb54dc254d59ac484a8d6a2f"
        const val SECRETE_KEY = "t2ivQakqcZ/cvxzekT7Ra9Ja8J1N1lBKu6LqVkijMliEeoD1lLXU0Qei+V9AC8aMbNG+TjVkca70NqFB9akmSg=="
        // 갤러리 권한 요청
        const val ADDR_RESULT = 1002

        val REQUEST_STORAGE_PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )

        val REQUEST_LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )

        fun Base64ToBitmaps(image: String): Bitmap {
            val encodedByte: ByteArray = Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodedByte, 0, encodedByte.size)
        }

        fun createRequestBody(text: String): RequestBody {
            return text.toRequestBody("text/plain".toMediaTypeOrNull())
        }

        fun createMultipartBodyPart(imgUri: Uri, name: String, context: Context): MultipartBody.Part {
            // 이미지 절대경로 반환 후 파일 객체 생성
            val file = File(absolutelyPath(imgUri, context))
            // 파일 객체를 RequestBody Type으로 변환
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            // MultipartBody.Part Type으로 변환

            return MultipartBody.Part.createFormData(name, file.name, requestFile)
        }

        // 절대경로 변환
        fun absolutelyPath(path: Uri, context : Context): String {
            val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
            val c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
            val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            c?.moveToFirst()

            val result = c?.getString(index!!)

            return result!!
        }

        fun isAllPermissionsGranted(context: Context, permissions: Array<String>): Boolean = permissions.all { permission->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }

        fun showSoftInput(context: Context, focusView: TextInputEditText) {
            CoroutineScope(Dispatchers.IO).launch {
                delay(100)
                val inputMethodManager = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(focusView, 0)
            }
        }

        fun hideSoftInput(context: Context, focusView: TextInputEditText) {
            CoroutineScope(Dispatchers.IO).launch {
                delay(100)
                val inputMethodManager = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(focusView.windowToken, 0)
            }
        }

        fun requestPlzInputText(msg: String, v: TextInputLayout){
            v.helperText = msg
            v.requestFocus()
        }

        fun setHelperBoxBlack(context: Context) = ContextCompat.getColor(context, R.color.helper_box_color_black)
        fun setHelperTextRed(context: Context) = ContextCompat.getColor(context, R.color.helper_text_color_red)
        fun setHelperTextRedList(context: Context) = ContextCompat.getColorStateList(context, R.color.helper_text_color_red)
        fun setHelperTextGreen(context: Context) = ContextCompat.getColor(context, R.color.helper_text_color_green)
        fun setHelperTextGreenList(context: Context) = ContextCompat.getColorStateList(context, R.color.helper_text_color_green)
    }

}