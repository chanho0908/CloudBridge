package com.myproject.cloudbridge.ui.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kakao.sdk.user.UserApiClient
import com.myproject.cloudbridge.databinding.FragmentJoinBinding
import com.myproject.cloudbridge.model.user.User
import com.myproject.cloudbridge.ui.main.MainActivity
import com.myproject.cloudbridge.viewmodel.IntroViewModel
import java.util.regex.Pattern


class JoinFragment : Fragment() {
    private var _binding: FragmentJoinBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IntroViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJoinBinding.inflate(inflater, container, false)
        UserApiClient.instance.me { user, _ ->
            viewModel.isJoinUser(user?.kakaoAccount?.email.toString())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            userIDEdit.addTextChangedListener {
                val id = it.toString()

                when (isValidUserID(id)) {
                    0 -> userIDLayout.helperText = "아이디를 입력해 주세요"
                    1 -> userIDLayout.helperText = "아이디는 7~12자 입니다."
                    2 -> {
                        userIDLayout.helperText = ""
                        viewModel.isUsingId(id)
                    }
                }

            }

            userPwdEdit.addTextChangedListener {
                val password = it.toString()

                when (isValidPassword(password)) {
                    0 -> userPwdLayout.helperText = "비밀번호를 입력해 주세요"
                    1 -> userPwdLayout.helperText = "숫자/영문/특수문자 8~ 16자로 입력해주세요."
                    2 -> userPwdLayout.helperText = ""
                }
            }

            userNameEdit.addTextChangedListener {
                val input = it.toString()
                if (input.isEmpty()) userNameLayout.helperText = "이름을 입력해 주세요"
                else userNameLayout.helperText = ""
            }

            phoneEdit.addTextChangedListener {
                val phone = it.toString()

                when (isValidPhoneNumber(phone)) {
                    0 -> phoneLayout.helperText = "핸드폰 번호를 모두 입력해 주세요"
                    1 -> phoneLayout.helperText = "숫자만 입력해 주세요"
                    2 -> phoneLayout.helperText = ""
                }
            }

            btnSubmit.setOnClickListener {
                val userID = userIDEdit.text.toString()
                val userPassword = userPwdEdit.text.toString()
                val userName = userNameEdit.text.toString()
                val phone = phoneEdit.text.toString()

                viewModel.isJoin.observe(viewLifecycleOwner) { isJoin ->
                    Log.d("SignOrLoginActivity", "isJoin: $isJoin.toString()")
                    when (isJoin) {
                        true ->
                            Toast.makeText(requireContext(), "이미 가입된 정보가 있습니다.", Toast.LENGTH_SHORT)
                                .show()

                        false -> {
                            viewModel.isUsingId.observe(viewLifecycleOwner) { isUsingId ->
                                Log.d("SignOrLoginActivity", "isUsingID: $isUsingId.toString()")
                                when (isUsingId) {
                                    true -> binding.userIDLayout.helperText = "이미 사용 중인 아이디 입니다."
                                    false -> {
                                        binding.userIDLayout.helperText = ""
                                        if (checkFinalValid(
                                                userID,
                                                userPassword,
                                                userName,
                                                phone
                                            )) {
                                            UserApiClient.instance.me { user, _ ->
                                                val email = user?.kakaoAccount?.email.toString()
                                                viewModel.writeNewUser(
                                                    User(
                                                        email,
                                                        userID,
                                                        userPassword,
                                                        userName,
                                                        phone
                                                    )
                                                )
                                                viewModel.setUpFirstFlag()
                                                viewModel.setUserID(userID)

                                                startActivity(
                                                    Intent(requireContext(),
                                                        MainActivity::class.java
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isValidUserID(userID: String): Int =
        if (userID.isEmpty()) 0
        else if (!Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{7,12}\$", userID)) 1
        else 2


    private fun isValidPassword(password: String): Int =
        if (password.isEmpty()) 0
        else if (!Pattern.matches(
                "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,20}$",
                password)) 1
        else 2



    private fun isValidPhoneNumber(phone: String): Int =
        if (phone.isEmpty()) 0
        else if (!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{4})\\d{4}$", phone)) 1
        else 2


    private fun checkFinalValid(userID: String, password: String, userName: String, phone: String): Boolean
    = (isValidUserID(userID) == 2 && isValidPassword(password) == 2 && userName.isNotEmpty()
                && isValidPhoneNumber(phone) == 2)



}