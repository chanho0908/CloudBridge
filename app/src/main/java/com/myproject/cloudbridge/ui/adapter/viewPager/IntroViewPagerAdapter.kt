package com.myproject.cloudbridge.ui.adapter.viewPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.myproject.cloudbridge.ui.user.JoinFragment
import com.myproject.cloudbridge.ui.user.SignFragment

class IntroViewPagerAdapter(fragment: FragmentActivity): FragmentStateAdapter(fragment) {

    var fragmentList: MutableList<Fragment> = ArrayList()

    init {
        fragmentList.add(SignFragment())
        fragmentList.add(JoinFragment())
    }

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SignFragment()
            else -> JoinFragment()
        }
    }


}