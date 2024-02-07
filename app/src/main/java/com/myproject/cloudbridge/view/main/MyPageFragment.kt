package com.myproject.cloudbridge.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
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

        // 프래그먼트가 살아있을 때
        // CoroutineScope(Dispatchers.IO) 처럼 직접 사용하는 것보다
        // 생명주기를 따르는 스코프를 생성하는 것이 좋다
        lifecycleScope.launch {
            MyDataStore().getCrn().collect { crn ->
                this@MyPageFragment.crn = crn
            }
        }

//        CoroutineScope(Dispatchers.IO).launch {
//            // crn 이 변경되는 값을 구독 시작
//            // IO 스레드가 계속 값을 구독
//            // 스레드가 계속 할일이있기 때문에 사라지지 않음
//            // 프래그먼트나 액티비티 가 종료되도 계속 살아있을 가능성이 있다.
//
//            MyDataStore().getCrn().collect{ crn->
//                this@MyPageFragment.crn = crn
//            }
//        }
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
                    startActivity(Intent(requireActivity(), MyStoreActivity::class.java))
                }
            }
        }
    }
}