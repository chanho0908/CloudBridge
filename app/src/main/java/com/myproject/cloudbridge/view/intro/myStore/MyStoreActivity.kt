package com.myproject.cloudbridge.view.intro.myStore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.ActivityMyStoreBinding
import com.myproject.cloudbridge.view.intro.myPage.NotRegistsedStoreActivity
import com.myproject.cloudbridge.view.main.MainActivity
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyStoreActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMyStoreBinding
    private val viewModel: StoreManagementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_store)

        initView()

        lifecycleScope.launch {
            observeDeleteStoreComplete()
        }
    }

    private fun initView(){

        // 기본값이 null이라 형변환 해줘야함
        val flag = intent.getStringExtra("FLAG").toString()

        initToolbar(flag)

        viewModel.getMyStoreInfo()

        with(binding){
            vm = viewModel
            lifecycleOwner = this@MyStoreActivity

            btnUpdate.setOnClickListener(this@MyStoreActivity)
            btnDelete.setOnClickListener(this@MyStoreActivity)
        }
    }

    private fun initToolbar(flag: String){

        if (flag == "REGISTER"){
            viewModel.fromServerToRoomSetAllStoreList()
            binding.materialToolbar.setNavigationOnClickListener {
                val intent = Intent(this@MyStoreActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        }else{
            binding.materialToolbar.setNavigationOnClickListener {
                finish()
            }
        }
    }

    private suspend fun observeDeleteStoreComplete(){
        viewModel.flag.collectLatest {
            if (it){
                val intent = Intent(this@MyStoreActivity, NotRegistsedStoreActivity::class.java)
                intent.putExtra("FLAG", "DELETE")
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showDialog(){
        val builder = MaterialAlertDialogBuilder(this@MyStoreActivity).apply {
            setTitle("매장 삭제")
            setMessage("매장을 삭제 하시겠습니까?")
            setPositiveButton("네") { _, _ ->
                viewModel.deleteMyStore()
            }
            setNegativeButton("아니오") { _, _ -> }
            create()
        }

        builder.show()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnUpdate -> {
                startActivity(Intent(this@MyStoreActivity, StoreUpdateActivity::class.java))
            }
            R.id.btnDelete -> {
                showDialog()
            }
        }
    }
}