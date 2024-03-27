package com.myproject.cloudbridge.ui.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.myproject.cloudbridge.databinding.ActivitySearchBinding
import com.myproject.cloudbridge.repository.LocalRepository
import com.myproject.cloudbridge.ui.search.vm.SearchViewModel
import com.myproject.cloudbridge.ui.search.vm.SearchViewModelFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    lateinit var viewModel: SearchViewModel
    lateinit var viewModelFactory: SearchViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
    }

    private fun initViewModel(){
        viewModelFactory = SearchViewModelFactory(LocalRepository())
        viewModel = ViewModelProvider(this, viewModelFactory)[SearchViewModel::class.java]
    }
}