package com.myproject.cloudbridge.adapter.binding

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

//기존 뷰들에 없는 새로운 xml 속성을 연결하는 기능 메소드를 가지는 객체
//보통 static 메소드를 가진 class로 사용
object MyStoreBindingAdapter {  //static 메소드를 가져야하기 때문에 class면 안됨 object로 명시

    // 1) 이미지뷰에 새로운 xml 속성 만들기
    // [속성명 : myStoreImage ]
    @JvmStatic
    @BindingAdapter("myStoreImage") //어노테이션 해독기 필요 - 빌드 그래이들에 기능 추가 필요!(kapt)
    fun imageBindingAdapter(view: ImageView, image: Bitmap?){
        if (image != null) {
            Glide.with(view.context).load(image).into(view)
        }
    }
}
