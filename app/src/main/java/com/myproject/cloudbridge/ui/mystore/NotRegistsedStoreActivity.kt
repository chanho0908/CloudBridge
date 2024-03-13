package com.myproject.cloudbridge.ui.mystore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.myproject.cloudbridge.databinding.ActivityNotRegistsedStoreBinding
import com.myproject.cloudbridge.ui.main.MainActivity
import com.myproject.cloudbridge.ui.store_registration.RegisteStoreActivity

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