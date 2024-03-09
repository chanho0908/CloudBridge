package com.myproject.cloudbridge.ui.main

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.SystemClock
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationBarView
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.ActivityMainBinding
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private val storeListFragment = StoreListFragment()
    private val mapFragment = MapFragment()
    private val myPageFragment = MyPageFragment()
    private val awardFragment = AwardFragment()

    lateinit var parentActivitySharedViewModel: StoreManagementViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        parentActivitySharedViewModel = ViewModelProvider(this)[StoreManagementViewModel::class.java]
        initFragment()
    }

    private fun initFragment(){
        binding.navigationView.setOnItemSelectedListener(this)
        setCurrentFragment(storeListFragment)
        parentActivitySharedViewModel.fetchAllStoreFromRoom()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.homeFragment -> {
                setCurrentFragment(storeListFragment)
                true
            }
            R.id.awardFragment -> {
                setCurrentFragment(awardFragment)
                true
            }
            R.id.mapFragment -> {
                setCurrentFragment(mapFragment)
                true
            }
            R.id.myPageFragment -> {
                setCurrentFragment(myPageFragment)
                true
            }
            else -> false
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        SystemClock.sleep(200)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
    }
}

