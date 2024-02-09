package com.myproject.cloudbridge

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.myproject.cloudbridge.databinding.ActivityTestBinding
import com.myproject.cloudbridge.util.locationProvider.FusedLocationProvider
import com.myproject.cloudbridge.util.locationProvider.OnLocationUpdateListener

class TestActivity: AppCompatActivity() {

    private val viewModel: TestViewModel by viewModels()
    private lateinit var binding: ActivityTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityTestBinding>(
            this,
            R.layout.activity_test
        ).apply {
                vm = viewModel
                lifecycleOwner = this@TestActivity
        }


    }

}