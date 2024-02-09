package com.myproject.cloudbridge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TestViewModel: ViewModel() {
    private var _humorText = MutableLiveData<String>()
    val humorText: LiveData<String> get() = _humorText

    init { _humorText.value = "" }
}

