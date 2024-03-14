package com.myproject.cloudbridge.ui.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.myproject.cloudbridge.databinding.ActivitySignOrLoginBinding

class SignOrLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignOrLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignOrLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = IntroViewPagerAdapter(this)

        // Tab과 ViewPager를 synchronize
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = "LOGIN"
                1 -> tab.text = "CREATE ACCOUNT"
            }
        }.attach()

    }
}