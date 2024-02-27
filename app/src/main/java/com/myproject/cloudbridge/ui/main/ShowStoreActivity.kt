package com.myproject.cloudbridge.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.ActivityMainBinding

class ShowStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_store)
    }
}