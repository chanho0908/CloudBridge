package com.myproject.cloudbridge.ui.mystore.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myproject.cloudbridge.repository.LocalRepository
import com.myproject.cloudbridge.repository.NetworkRepository
import java.lang.IllegalArgumentException

class StoreManagementViewModelFactory: ViewModelProvider.Factory {
    private val networkRepository = NetworkRepository()
    private val localRepository = LocalRepository()
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(StoreManagementViewModel::class.java)){
            return StoreManagementViewModel(networkRepository, localRepository) as T
        }

        throw IllegalArgumentException("unknown ViewModel class")
    }
}