package com.myproject.cloudbridge.ui.storemenu.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.cloudbridge.model.store.StoreMenuRvModel
import com.myproject.cloudbridge.repository.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoreMenuManagementViewModel(private val repository: NetworkRepository): ViewModel() {
    private var _menuList = MutableStateFlow<ArrayList<StoreMenuRvModel>?>(null)
    val menuList = _menuList.asStateFlow()

    val newList = ArrayList<StoreMenuRvModel>()
    fun addMenu(menu: StoreMenuRvModel) = viewModelScope.launch {

    }
}