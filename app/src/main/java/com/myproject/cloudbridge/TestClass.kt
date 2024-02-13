package com.myproject.cloudbridge

import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.myproject.cloudbridge.db.entity.StoreEntity
import com.myproject.cloudbridge.util.Utils.requestPlzInputText

object TestClass: UserModifyInputHandler {

    // BindingAdpater 가 View에 data 를 set 해주는 setter 역할
    // 사용자의 입력을 처리하거나 UI를 업데이트하는 데 사용
    @BindingAdapter("observeUserInputStoreName")
    override fun setUserInputModifyData(view: TextInputEditText, storeEntity: StoreEntity?) {
        storeEntity?.let {
            view.setText(it.storeName)
        }
    }

    // BindingAdpater 가 View에 data 를 set 해주는 setter 역할
    // 사용자의 입력을 처리하거나 UI를 업데이트하는 데 사용
    override fun setUserInputModifyDataInverseBindingListener(
        view: TextInputEditText,
        listener: InverseBindingListener
    ) {
        val parentLayout = view.rootView.findViewById<TextInputLayout>(R.id.humor_input_layout)

        view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                listener.onChange()
                val input = s.toString()
                s?.let {
                    if (input.isEmpty()){
                        requestPlzInputText("매장명을 입력해 주세요.", parentLayout)
                    }else{
                        parentLayout.helperText = ""

                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // View에 data 를 가져오는 getter 역할
    // event 는 data 가 바뀌어서 InverseBindingListener 의 onChange() 함수가 호출되는 부분
    // BindingAdpater와 InverseBindingListener를 연결해준다
    override fun getUserInputModifyData(view: TextInputEditText): String {
        return view.text.toString()
    }
}