package com.myproject.cloudbridge.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.dataStore.MyDataStore
import com.myproject.cloudbridge.databinding.FragmentMyPageBinding
import com.myproject.cloudbridge.view.intro.myPage.MyInfoActivity
import com.myproject.cloudbridge.view.intro.myPage.NotRegistsedStoreActivity
import com.myproject.cloudbridge.view.intro.myStore.MyStoreActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyPageFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentMyPageBinding ?= null
    private val binding get() = _binding!!
    private var crn: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            btnMyInfo.setOnClickListener(this@MyPageFragment)
            btnMyStoreInfo.setOnClickListener(this@MyPageFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            MyDataStore().getCrn().collect { crn ->
                this@MyPageFragment.crn = crn
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnMyInfo -> {
                //startActivity(Intent(requireContext(), MyInfoActivity::class.java))
            }
            R.id.btnMyStoreInfo -> {
                if (crn == ""){
                    startActivity(Intent(requireActivity(), NotRegistsedStoreActivity::class.java))
                }else{
                    Log.d("dasd", crn)
                    startActivity(Intent(requireActivity(), MyStoreActivity::class.java))
                }
            }
        }
    }
}