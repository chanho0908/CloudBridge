package com.myproject.cloudbridge.ui.search.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.cloudbridge.datasource.local.entity.RecentlySearchKeywordEntity
import com.myproject.cloudbridge.repository.LocalRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: LocalRepository) : ViewModel() {

    private val _allKeyWord = MutableStateFlow<List<RecentlySearchKeywordEntity>>(emptyList())
    val allKeyWord = _allKeyWord.asStateFlow()

    init {
        getAllKeyWord()
    }

    private fun getAllKeyWord() = viewModelScope.launch {
        repository.readAllKeyword().collect {
            _allKeyWord.value = it
        }
    }

    fun insertKeyword(keyword: RecentlySearchKeywordEntity) = viewModelScope.launch {
        val currentKeywords = allKeyWord.value
        if (currentKeywords.contains(keyword)){
            deleteKeyword(keyword.keyword).join()
        }
        if (currentKeywords.size > 5){
            val lastKeyword = allKeyWord.value[currentKeywords.size -1]
            deleteKeyword(lastKeyword.keyword).join()
        }

        repository.insertKeyword(keyword)

    }

    fun deleteKeyword(keyword: String)  = viewModelScope.launch {
        repository.deleteKeyword(keyword)
    }

    fun deleteAllKeyword() = viewModelScope.launch {
        repository.deleteAllKeyword()
    }
}