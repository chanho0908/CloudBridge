package com.myproject.cloudbridge.utility

import android.Manifest
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlin.concurrent.thread

object Utils {

    val REQUEST_IMAGE_PERMISSIONS by lazy{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    val REQUEST_LOCATION_PERMISSIONS by lazy {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }
    val LOCATION_PERMISSION_REQUEST_CODE by lazy { 100 }
    val ADDR_RESULT_RESULT_CODE by lazy { 1002 }
    val APP_KEY by lazy { "c23ff52edb54dc254d59ac484a8d6a2f" }

    // 사업자 등록 번호 API KEY
    val SECRETE_KEY by lazy { "t2ivQakqcZ/cvxzekT7Ra9Ja8J1N1lBKu6LqVkijMliEeoD1lLXU0Qei+V9AC8aMbNG+TjVkca70NqFB9akmSg==" }

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

    fun requestPlzInputText(msg: String, v: TextInputLayout){
        v.helperText = msg
        v.requestFocus()
    }

    fun showSoftInput(view: View){
        // 뷰에 포커스를 준다.
        view.requestFocus()
        thread {
            // 딜레이
            SystemClock.sleep(200)
            // 키보드 관리 객체를 가져온다.
            val inputMethodManger = getContext().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            // 키보드를 올린다.
            inputMethodManger.showSoftInput(view, 0)
        }
    }

    private fun getContext(): Context = App.context()
}