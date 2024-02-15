package com.myproject.cloudbridge.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.dataStore.MainDataStore
import com.myproject.cloudbridge.databinding.FragmentMyPageBinding
import com.myproject.cloudbridge.view.intro.myPage.MyInfoActivity
import com.myproject.cloudbridge.view.intro.myPage.NotRegistsedStoreActivity
import com.myproject.cloudbridge.view.intro.myStore.MyStoreActivity
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MyPageFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentMyPageBinding ?= null
    private val binding get() = _binding!!

    private val viewModel: StoreManagementViewModel by viewModels()
    private var isClicked = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView(){
        with(binding) {
            btnMyInfo.setOnClickListener(this@MyPageFragment)
            btnMyStoreInfo.setOnClickListener(this@MyPageFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.myCompanyRegistrationNumber.collect{
                    if (isClicked){
                        if (it == ""){
                            startActivity(Intent(requireContext(), NotRegistsedStoreActivity::class.java))
                        }else{
                            startActivity(Intent(requireContext(), MyStoreActivity::class.java))
                        }
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnMyInfo -> {
                //startActivity(Intent(mContext, MyInfoActivity::class.java))
            }
            R.id.btnMyStoreInfo -> {
                isClicked = true
                viewModel.getMySavedCompanyRegistrationNumber()
            }
        }
    }
}