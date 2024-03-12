package com.myproject.cloudbridge.viewmodel

import androidx.lifecycle.ViewModel
import com.myproject.cloudbridge.model.store.StoreMenuRvModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StoreMenuManagementViewModel: ViewModel() {
    private var _menuList = MutableStateFlow<ArrayList<StoreMenuRvModel>?>(null)
    val menuList = _menuList.asStateFlow()

    fun requestStoreMenuInput(){

    }

}