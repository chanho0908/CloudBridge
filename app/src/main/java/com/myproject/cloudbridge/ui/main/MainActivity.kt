package com.myproject.cloudbridge.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.ActivityMainBinding
import com.myproject.cloudbridge.repository.LocalRepository
import com.myproject.cloudbridge.repository.NetworkRepository
import com.myproject.cloudbridge.ui.mystore.vm.StoreManagementViewModel
import com.myproject.cloudbridge.ui.mystore.vm.StoreManagementViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    // 접근 제한자가 private 일 경우 Fragment에서 참조 불가
    lateinit var viewModel: StoreManagementViewModel
    lateinit var viewModelFactory: StoreManagementViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFragment()
    }

    private fun initFragment(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.navigationView, navController)

        viewModelFactory = StoreManagementViewModelFactory(NetworkRepository(), LocalRepository())
        viewModel = ViewModelProvider(this, viewModelFactory)[StoreManagementViewModel::class.java]
        viewModel.fetchAllStoreFromRoom()
    }
}

