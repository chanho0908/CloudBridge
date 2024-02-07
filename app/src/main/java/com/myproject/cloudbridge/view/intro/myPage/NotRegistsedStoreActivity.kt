package com.myproject.cloudbridge.view.intro.myPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.ActivityNotRegistsedStoreBinding
import com.myproject.cloudbridge.view.main.MainActivity
import com.myproject.cloudbridge.view.storeRegistration.RegisteStoreActivity
import com.myproject.cloudbridge.viewModel.MyPageViewModel

class NotRegistsedStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotRegistsedStoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotRegistsedStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val request = intent.getStringExtra("FLAG").toString()


        if (request == "DELETE"){
            binding.materialToolbar.setNavigationOnClickListener {
                val intent = Intent(this@NotRegistsedStoreActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        }else{
            binding.materialToolbar.setNavigationOnClickListener { finish() }
        }

        binding.btnUpdate.setOnClickListener {
            startActivity(Intent(this, RegisteStoreActivity::class.java))
        }
    }
}