package com.myproject.cloudbridge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.cloudbridge.datasource.local.entity.RecentlySearchKeywordEntity
import com.myproject.cloudbridge.repository.LocalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: LocalRepository): ViewModel() {
    private val _allKeyWord = MutableStateFlow<List<RecentlySearchKeywordEntity>>(emptyList())
    val allKeyWord = _allKeyWord.asStateFlow()

    fun getAllKeyWord() = viewModelScope.launch {
        repository.readAllKeyword().stateIn(this).collect{
            _allKeyWord.value = it
        }
    }

    fun insertKeyword(keywordEntity: RecentlySearchKeywordEntity) = viewModelScope.launch {
        repository.insertKeyword(keywordEntity)
    }

    fun deleteKeyword(keyword: String) = viewModelScope.launch {
        repository.deleteKeyword(keyword)
    }

    fun deleteAllKeyword() = viewModelScope.launch {
        repository.deleteAllKeyword()
    }
}