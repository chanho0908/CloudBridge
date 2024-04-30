package com.myproject.cloudbridge.ui.search.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myproject.cloudbridge.repository.LocalRepository
import java.lang.IllegalArgumentException

class SearchViewModelFactory : ViewModelProvider.Factory {
    private val localRepository: LocalRepository = LocalRepository()
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)){
            return SearchViewModel(localRepository) as T
        }

        throw IllegalArgumentException("unknown ViewModel class")

    }
}