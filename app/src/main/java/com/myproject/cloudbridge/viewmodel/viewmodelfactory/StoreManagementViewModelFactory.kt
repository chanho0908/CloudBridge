package com.myproject.cloudbridge.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myproject.cloudbridge.repository.LocalRepository
import com.myproject.cloudbridge.repository.NetworkRepository
import com.myproject.cloudbridge.viewmodel.StoreManagementViewModel
import java.lang.IllegalArgumentException

class StoreManagementViewModelFactory(
    private val networkRepository: NetworkRepository,
    private val localRepository: LocalRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(StoreManagementViewModel::class.java)){
            return StoreManagementViewModel(networkRepository, localRepository) as T
        }

        throw IllegalArgumentException("unknown ViewModel class")
    }
}