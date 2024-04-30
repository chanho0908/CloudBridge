package com.myproject.cloudbridge.ui.search.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import com.myproject.cloudbridge.datasource.local.entity.RecentlySearchKeywordEntity
import com.myproject.cloudbridge.repository.LocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: LocalRepository) : ViewModel() {

    private val _allKeyWord = MutableStateFlow<List<RecentlySearchKeywordEntity>>(emptyList())
    val allKeyWord = _allKeyWord.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    @FlowPreview
    @ExperimentalCoroutinesApi
    val autoCompleteKeywordResult = _searchQuery
        .debounce(500)
        .flatMapLatest { query ->
            if (query.isNotBlank()) {
                repository.getAutoCompleteSearchResult(query)
            } else{
                flowOf()
            }
        }
        .flowOn(Dispatchers.IO)
        .catch { e: Throwable ->
            e.printStackTrace()
        }

    init {
        getAllKeyWord()
    }

    private fun getAllKeyWord() = viewModelScope.launch {
        repository.readAllKeyword().stateIn(this, SharingStarted.WhileSubscribed(5000), emptyList()).collect {
            _allKeyWord.value = it
        }
    }

    fun insertKeyword(newKeyword: RecentlySearchKeywordEntity) = viewModelScope.launch {
        val currentKeywords = allKeyWord.value
        currentKeywords.forEach { oldKeyword ->
            if (oldKeyword.keyword == newKeyword.keyword) {
                deleteKeyword(oldKeyword.id).join()
            }
        }

        repository.insertKeyword(newKeyword)
    }

    fun deleteKeyword(id: Long) = viewModelScope.launch {
        repository.deleteKeyword(id)
    }

    fun deleteAllKeyword() = viewModelScope.launch {
        repository.deleteAllKeyword()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }


}