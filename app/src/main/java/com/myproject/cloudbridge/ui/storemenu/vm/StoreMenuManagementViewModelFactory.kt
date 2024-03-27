package com.myproject.cloudbridge.ui.storemenu.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myproject.cloudbridge.repository.NetworkRepository
import com.myproject.cloudbridge.ui.search.vm.SearchViewModel
import java.lang.IllegalArgumentException

class StoreMenuManagementViewModelFactory(private val repository: NetworkRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoreMenuManagementViewModel::class.java)){
            return StoreMenuManagementViewModel(repository) as T
        }

        throw IllegalArgumentException("unknown ViewModel class")
    }
}