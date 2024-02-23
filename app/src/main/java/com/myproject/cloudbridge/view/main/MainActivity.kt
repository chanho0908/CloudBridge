package com.myproject.cloudbridge.view.main

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private val storeListFragment = StoreListFragment()
    private val mapFragment = MapFragment()
    private val myPageFragment = MyPageFragment()
    private val awardFragment = AwardFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationView.setOnItemSelectedListener(this)
        setCurrentFragment(storeListFragment)

        val navigationView = binding.navigationView
        // 색상 리소스 ID에서 실제 색상 가져오기
        // 색상 리소스 ID에서 실제 색상 가져오기
        val colorWhite = ContextCompat.getColor(this, R.color._white)

        // ColorDrawable로 변환하여 Drawable 변수에 할당

        // ColorDrawable로 변환하여 Drawable 변수에 할당
        val backgroundDrawable: Drawable = ColorDrawable(colorWhite)
        navigationView.itemBackground = backgroundDrawable


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
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
    }
}

