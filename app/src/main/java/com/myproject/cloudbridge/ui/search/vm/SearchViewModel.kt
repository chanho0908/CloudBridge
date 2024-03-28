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
        repository.readAllKeyword().stateIn(this).collect {
            _allKeyWord.value = it
        }
    }
    fun insertKeyword(newKeyword: RecentlySearchKeywordEntity) = viewModelScope.launch {
        val currentKeywords = allKeyWord.value
        currentKeywords.forEach { oldKeyword->
            if (oldKeyword.keyword == newKeyword.keyword){
                deleteKeyword(oldKeyword.id).join()
            }
        }

        repository.insertKeyword(newKeyword)
    }

    fun deleteKeyword(id: Long)  = viewModelScope.launch {
        repository.deleteKeyword(id)
    }

    fun deleteAllKeyword() = viewModelScope.launch {
        repository.deleteAllKeyword()
    }
}