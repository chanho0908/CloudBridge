package com.myproject.cloudbridge

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.annotation.Dimension
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.myproject.cloudbridge.util.Constants.Companion.requestPlzInputText
import com.myproject.cloudbridge.util.Constants.Companion.setHelperTextRedList

object TestClass {

    // BindingAdpater 가 View에 data 를 set 해주는 setter 역할
    // 사용자의 입력을 처리하거나 UI를 업데이트하는 데 사용
    @JvmStatic
    @BindingAdapter("observeUserInputText")
    fun setFlexibleSizeText(view: TextInputEditText, text: String) {
    }

    // BindingAdapter 의 이름에 "AttrChanged" 를 붙여서 작명하는 것이 원칙
    // InverseBindingListener 의 onChange() 함수가 어디서 호출 되는지를 정의
    // InverseBindingListener 는 xml 이 빌드될 때 생성되는 Binding Code 에서 등록되는데,
    // view 의 데이터가 변경되었을 시 onChanged() 를 호출하는 리스너
    @JvmStatic
    @BindingAdapter("observeUserInputTextAttrChanged")
    fun setFlexibleSizeTextInverseBindingListener(view: TextInputEditText, listener: InverseBindingListener) {
        val parentLayout = view.rootView.findViewById<TextInputLayout>(R.id.humor_input_layout)

        view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                listener.onChange()
                val input = s.toString()
                s?.let {
                    if (input.isEmpty()){
                        requestPlzInputText("최대 10글자 입니다.", parentLayout)
                    }else{
                        parentLayout.helperText = ""
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    // View에 data 를 가져오는 getter 역할
    // event 는 data 가 바뀌어서 InverseBindingListener 의 onChange() 함수가 호출되는 부분
    // BindingAdpater와 InverseBindingListener를 연결해준다
    @JvmStatic
    @InverseBindingAdapter(attribute = "observeUserInputText", event = "observeUserInputTextAttrChanged")
    fun getFlexibleSizeText(view: TextInputEditText): String {
        return view.text.toString()
    }
}