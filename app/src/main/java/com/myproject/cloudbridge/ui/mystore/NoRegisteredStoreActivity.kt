package com.myproject.cloudbridge.ui.mystore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.ActivityNoRegisteredStoreBinding
import com.myproject.cloudbridge.ui.main.MainActivity
import com.myproject.cloudbridge.ui.store_registration.RegisteStoreActivity

class NoRegisteredStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoRegisteredStoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoRegisteredStoreBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_no_registered_store)

        val request = intent.getStringExtra("FLAG").toString()

        if (request == "DELETE"){
            binding.materialToolbar.setNavigationOnClickListener {
                val intent = Intent(this@NoRegisteredStoreActivity, MainActivity::class.java)
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