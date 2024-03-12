package com.myproject.cloudbridge.ui.mypage

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.myproject.cloudbridge.databinding.ActivityDeleteUserBinding
import com.myproject.cloudbridge.datasource.local.entity.UserEntity
import com.myproject.cloudbridge.viewmodel.UserManagementViewModel
import kotlinx.coroutines.launch

class DeleteUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteUserBinding
    private lateinit var userEntity: UserEntity
    private val viewModel: UserManagementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getUserProfile()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.userProfile.collect{ userProfile->
                    userEntity = UserEntity(
                        userProfile[0].userKakaoEmail,
                        userProfile[0].userID,
                        userProfile[0].userPw,
                        userProfile[0].userName,
                        userProfile[0].userPhone,
                    )
                }
            }
        }

        binding.apply {

            materialToolbar.setNavigationOnClickListener { finish() }

            btnUserDelete.setOnClickListener {
                val inputPassword = binding.userPwdEdit.text.toString()

                if (inputPassword.isEmpty()) binding.userPwdLayout.helperText = "비밀번호를 입력해 주세요"
                else if (!inputPassword.equals(userEntity.userPw)) binding.userPwdLayout.helperText =
                    "비밀번호가 틀립니다."
                else {
                    val builder = AlertDialog.Builder(this@DeleteUserActivity)

                    //dialog UI세팅
                    builder.setTitle("회원 탈퇴")
                        .setMessage("${userEntity.userName}님 정말 탈퇴 하시겠습니까?")
                        .setPositiveButton("네") { dialog, id ->
                            Toast.makeText(this@DeleteUserActivity, "탈퇴 완료", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@DeleteUserActivity, FarewellActivity::class.java)
                            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)
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
}