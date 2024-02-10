package com.myproject.cloudbridge

import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputEditText
import com.myproject.cloudbridge.db.entity.StoreEntity

interface UserModifyInputHandler {
    fun setUserInputModifyData(view: TextInputEditText, storeEntity: StoreEntity?)
    fun setUserInputModifyDataInverseBindingListener(view: TextInputEditText, listener: InverseBindingListener)
    fun getUserInputModifyData(view: TextInputEditText): String
}