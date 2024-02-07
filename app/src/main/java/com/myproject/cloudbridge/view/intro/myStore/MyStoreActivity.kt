package com.myproject.cloudbridge.view.intro.myStore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.ActivityMyStoreBinding
import com.myproject.cloudbridge.view.intro.myPage.NotRegistsedStoreActivity
import com.myproject.cloudbridge.view.main.MainActivity
import com.myproject.cloudbridge.viewModel.MainViewModel
import com.myproject.cloudbridge.viewModel.MyPageViewModel
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class MyStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyStoreBinding
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val mViewModel: MainViewModel by viewModels()
    private val storeManagementViewModel: StoreManagementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_store)
        binding.vm = myPageViewModel
        binding.lifecycleOwner = this


        // 기본값이 null이라 형변환 해줘야함
        val flag = intent.getStringExtra("FLAG").toString()

        initToolbar(flag)

        myPageViewModel.getMyStoreInfo()

        binding.apply{
            btnUpdate.setOnClickListener {
                startActivity(Intent(this@MyStoreActivity, StoreUpdateActivity::class.java))
            }
            btnDelete.setOnClickListener {
                showDialog()
            }
        }
    }


    private fun initToolbar(flag: String){

        if (flag == "REGISTER"){
            mViewModel.fromServerToRoomSetAllStoreList()
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

    private fun showDialog(){
        val builder = MaterialAlertDialogBuilder(this@MyStoreActivity).apply {
            setTitle("매장 삭제")
            setMessage("매장을 삭제 하시겠습니까?")
            setPositiveButton("네") { _, _ ->
                    storeManagementViewModel.deleteMyStore()

                    val intent = Intent(this@MyStoreActivity, NotRegistsedStoreActivity::class.java)
                    intent.putExtra("FLAG", "DELETE")
                    startActivity(intent)
                    finish()

            }
            setNegativeButton("아니오") { _, _ -> }
            create()
        }

        builder.show()
    }
}