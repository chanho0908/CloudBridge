package com.myproject.cloudbridge.ui.myPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kakao.sdk.user.UserApiClient
import com.myproject.cloudbridge.dataStore.MainDataStore
import com.myproject.cloudbridge.databinding.ActivityUpdateUserProfileBinding
import com.myproject.cloudbridge.localDB.entity.UserEntity
import com.myproject.cloudbridge.viewModel.UserManagementViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class UpdateUserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateUserProfileBinding
    private val viewModel: UserManagementViewModel by viewModels()
    private lateinit var savedUserPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getUserProfile()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userProfile.collect{ userProfile->
                    binding.userNameEdit.setText(userProfile[0].userName)
                    binding.userPhoneEdit.setText(userProfile[0].userPhone)
                    savedUserPassword = userProfile[0].userPw
                }
            }
        }

        with(binding){
            materialToolbar.setNavigationOnClickListener { finish() }

            userNameEdit.addTextChangedListener {
                val input = it.toString()
                if (input.isEmpty()) userNameLayout.helperText = "이름을 입력해 주세요"
                else userNameLayout.helperText = ""
            }

            userPhoneEdit.addTextChangedListener {
                val phone = it.toString()

                when(isValidPhoneNumber(phone)){
                    0 -> userPhoneLayout.helperText = "핸드폰 번호를 모두 입력해 주세요"
                    1 -> userPhoneLayout.helperText = "숫자만 입력해 주세요"
                    2 -> userPhoneLayout.helperText = ""
                }
            }

            userPwdEdit.addTextChangedListener {
                val password = it.toString()

                if (password.isEmpty()) userPwdLayout.helperText = "비밀번호를 입력해 주세요"
                else if (!savedUserPassword.equals(password)) userPwdLayout.helperText = "비밀번호가 다릅니다"
                else userPwdLayout.helperText = ""

            }

            userUpdatePwdEdit.addTextChangedListener {
                val password = it.toString()

                when(isValidPassword(password)){
                    0 -> userUpdatePwdLayout.helperText = "비밀번호를 입력해 주세요"
                    1 -> userUpdatePwdLayout.helperText = "숫자/영문/특수문자 8~ 16자로 입력해주세요."
                    2 -> userUpdatePwdLayout.helperText = ""
                }
            }

            btnUserUpdate.setOnClickListener {
                val userPassword = userPwdEdit.text.toString()
                val userUpdatePassword = userUpdatePwdEdit.text.toString()
                val userName = userNameEdit.text.toString()
                val userPhone = userPhoneEdit.text.toString()

                if (checkFinalValid(userPassword, userUpdatePassword, userName, userPhone)){
                    UserApiClient.instance.me { user, _ ->
                        val email = user?.kakaoAccount?.email.toString()

                        CoroutineScope(Dispatchers.IO).launch{
                            viewModel.updateUserProfile(
                                UserEntity(
                                email,
                                MainDataStore.getUserId(),
                                userUpdatePassword,
                                userName,
                                userPhone
                            ))
                        }
                    }
                    startActivity(Intent(this@UpdateUserProfileActivity, MyInfoActivity::class.java))
                    finish()

                }
            }
        }
    }

    private fun isValidPassword(password: String): Int =
        if (password.isEmpty()) 0
        else if (!Pattern.matches( "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,20}$", password)) 1
        else 2


    private fun isValidPhoneNumber(phone: String): Int =
        if (phone.isEmpty()) 0
        else if (!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{4})\\d{4}$", phone)) 1
        else 2


    private fun checkFinalValid(userPassword: String, UpdatePassword:String, userName: String, phone: String): Boolean =
        (isValidPassword(UpdatePassword) == 2 && isValidPhoneNumber(phone) == 2 && userName.isNotEmpty() &&
                savedUserPassword.equals(userPassword))


}