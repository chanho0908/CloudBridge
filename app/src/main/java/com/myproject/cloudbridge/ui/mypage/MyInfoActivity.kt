package com.myproject.cloudbridge.ui.mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.ActivityMyInfoBinding
import com.myproject.cloudbridge.ui.user.SignOrLoginActivity
import kotlinx.coroutines.launch


class MyInfoActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMyInfoBinding
    private val viewModel: UserManagementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        setListener()

    }

    private fun setListener(){
        binding.apply {
            materialToolbar.setNavigationOnClickListener { finish() }

            btnUpdate.setOnClickListener(this@MyInfoActivity)

            btnLogout.setOnClickListener(this@MyInfoActivity)

            btnDelete.setOnClickListener(this@MyInfoActivity)
        }
    }

    private fun initView(){
        viewModel.getUserProfile()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.userProfile.collect{ userProfile->
                    try {
                        binding.userEmailTextView.text = userProfile[0].userKakaoEmail
                        binding.userPhoneTextView.text = userProfile[0].userPhone
                        binding.userNameTextView.text = userProfile[0].userName
                        binding.userPwdTextView.text = "*".repeat(userProfile[0].userPw.length)
                    }catch (e: IndexOutOfBoundsException){
                        print(e.message)
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnUpdate -> {
                startActivity(Intent(this@MyInfoActivity, UpdateUserProfileActivity::class.java))
            }
            R.id.btnDelete -> {
                startActivity(Intent(this@MyInfoActivity, DeleteUserActivity::class.java))
            }
            R.id.btnLogout -> {
                val builder = MaterialAlertDialogBuilder(this@MyInfoActivity)

                //dialog UI세팅
                builder.setTitle("로그아웃")
                    .setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("네") { dialog, id ->

                        viewModel.logout()

                        Toast.makeText(this@MyInfoActivity, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@MyInfoActivity, SignOrLoginActivity::class.java))
                        finish()
                    }
                    .setNegativeButton("아니오") { dialog, id ->

                    }
                    .create()
                    .show()
            }
        }
    }
}