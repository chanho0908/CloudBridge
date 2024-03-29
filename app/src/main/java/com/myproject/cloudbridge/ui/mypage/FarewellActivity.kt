package com.myproject.cloudbridge.ui.mypage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.myproject.cloudbridge.databinding.ActivityFarewellBinding
import kotlin.system.exitProcess

class FarewellActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFarewellBinding
    private val viewModel: UserManagementViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFarewellBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            viewModel.deleteUser()
            ActivityCompat.finishAffinity(this) // 액티비티 종료
            exitProcess(0) // 프로세스 종료
        }
    }
}