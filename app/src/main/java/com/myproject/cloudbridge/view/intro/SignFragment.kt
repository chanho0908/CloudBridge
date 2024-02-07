package com.myproject.cloudbridge.view.intro

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.myproject.cloudbridge.databinding.FragmentSignBinding
import com.myproject.cloudbridge.db.entity.UserEntity
import com.myproject.cloudbridge.view.main.MainActivity
import com.myproject.cloudbridge.viewModel.IntroViewModel

class SignFragment : Fragment() {
    private var _binding: FragmentSignBinding ?= null
    private val binding get() = _binding!!
    private val viewModel: IntroViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.userIdEdit.addTextChangedListener {userID->
            viewModel.findUserData(userID.toString())
        }

        binding.btnSubmit.setOnClickListener {
            val userID = binding.userIdEdit.text.toString()
            val userPw = binding.userPwdEdit.text.toString()

            if (isValidUserID(userID)){
                binding.userIdLayout.helperText = "아이디 입력해 주세요"
            }else if (isValidUserPwd(userPw)){
                binding.userPwdLayout.helperText = "비밀번호를 입력해 주세요"
            }else{
                    viewModel.user.observe(viewLifecycleOwner) { user ->

                        if (user == null) {
                            binding.userIdLayout.helperText = "존재하지 않는 아이디 입니다."
                        }else if (user.userPw.equals(userPw)){

                            viewModel.signUpUser(
                                UserEntity(
                                userID,
                                user.userKakaoEmail.toString(),
                                user.userPw.toString(),
                                user.userName.toString(),
                                user.userPhone.toString()
                            )
                            )
                            viewModel.setUserID(userID)
                            startActivity(Intent(requireContext(), MainActivity::class.java))

                        }else{
                            binding.userPwdLayout.helperText = "비밀번호가 틀렸습니다."
                        }
                    }

        }}
    }

    private fun isValidUserID(userID: String): Boolean {
        return userID.isEmpty()
    }

    private fun isValidUserPwd(userPwd: String): Boolean {
        return userPwd.isEmpty()
    }

}