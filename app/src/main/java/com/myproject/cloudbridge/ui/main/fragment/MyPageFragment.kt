package com.myproject.cloudbridge.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentMyPageBinding
import com.myproject.cloudbridge.repository.LocalRepository
import com.myproject.cloudbridge.repository.NetworkRepository
import com.myproject.cloudbridge.ui.mystore.MyStoreActivity
import com.myproject.cloudbridge.ui.mystore.NoRegisteredStoreActivity
import com.myproject.cloudbridge.ui.mystore.vm.StoreManagementViewModel
import com.myproject.cloudbridge.ui.mystore.vm.StoreManagementViewModelFactory

class MyPageFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: StoreManagementViewModel
    private lateinit var viewModelFactory: StoreManagementViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyPageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        viewModelFactory = StoreManagementViewModelFactory(NetworkRepository(), LocalRepository())
        viewModel = ViewModelProvider(this, viewModelFactory)[StoreManagementViewModel::class.java]

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
                    startActivity(Intent(requireContext(), NoRegisteredStoreActivity::class.java))
                }
            }
        }
    }

}