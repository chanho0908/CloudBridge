package com.myproject.cloudbridge.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.ActivityMainBinding
import com.myproject.cloudbridge.ui.mystore.vm.StoreManagementViewModel
import com.myproject.cloudbridge.ui.mystore.vm.StoreManagementViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: StoreManagementViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFragment()
        initViewModel()
    }

    private fun initFragment(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.navigationView, navController)
    }

    private fun initViewModel(){
        val viewModelFactory = StoreManagementViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory)[StoreManagementViewModel::class.java]
        viewModel.fetchAllStoreFromRoom()
    }
}

