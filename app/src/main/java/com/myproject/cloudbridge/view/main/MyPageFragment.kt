package com.myproject.cloudbridge.view.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.dataStore.MainDataStore
import com.myproject.cloudbridge.databinding.FragmentMyPageBinding
import com.myproject.cloudbridge.view.intro.myPage.MyInfoActivity
import com.myproject.cloudbridge.view.intro.myPage.NotRegistsedStoreActivity
import com.myproject.cloudbridge.view.intro.myStore.MyStoreActivity
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel

class MyPageFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StoreManagementViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyPageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        with(binding) {
            btnMyInfo.setOnClickListener(this@MyPageFragment)
            btnMyStoreInfo.setOnClickListener(this@MyPageFragment)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnMyInfo -> {
                //startActivity(Intent(mContext, MyInfoActivity::class.java))
            }

            R.id.btnMyStoreInfo -> {

                val crn = viewModel.myCompanyRegistrationNumber.value

                if (crn?.length == 10) {
                    val intent = Intent(requireContext(), MyStoreActivity::class.java)
                    intent.putExtra("crn", crn)
                    startActivity(intent)
                }
                if (crn == "") {
                    startActivity(Intent(requireContext(), NotRegistsedStoreActivity::class.java))
                }
            }
        }
    }

}