package com.myproject.cloudbridge.view.intro.myStore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.ActivityMyStoreBinding
import com.myproject.cloudbridge.model.store.StoreInfoSettingModel
import com.myproject.cloudbridge.view.intro.myPage.NotRegistsedStoreActivity
import com.myproject.cloudbridge.view.main.MainActivity
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MyStoreActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMyStoreBinding
    private val viewModel: StoreManagementViewModel by viewModels()
    private lateinit var crn: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        initCoroutineProcess()
    }

    private fun initCoroutineProcess() {
        crn = intent.getStringExtra("crn").toString()
        viewModel.getMyStoreInfo()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.imgLoading.collect { loading ->
                    if (loading == true) {
                        viewModel.myStore.collect { store ->
                            initView(store)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.flag.collect {
                    if (it == true) {
                        val intent = Intent(this@MyStoreActivity, NotRegistsedStoreActivity::class.java)
                        intent.putExtra("FLAG", "DELETE")
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun initView(myStore: StoreInfoSettingModel) {
        with(binding) {
            storeCeoNameTextView.text = myStore.storeInfo.ceoName
            storeNameTextView.text = myStore.storeInfo.storeName
            storePhoneTextView.text = myStore.storeInfo.contact
            storeAddrTextView.text = myStore.storeInfo.address
            storeKindTextView.text = myStore.storeInfo.kind

            Glide.with(this@MyStoreActivity)
                .load(myStore.storeImage)
                .into(storeMainImage)

            btnUpdate.setOnClickListener(this@MyStoreActivity)
            btnDelete.setOnClickListener(this@MyStoreActivity)
        }
    }

    private fun initToolbar() {
        val flag = intent.getStringExtra("FLAG").toString()

        if (flag == "REGISTER") {
            binding.materialToolbar.setNavigationOnClickListener {
                val intent = Intent(this@MyStoreActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        } else {
            binding.materialToolbar.setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun showDialog() {
        val builder = MaterialAlertDialogBuilder(this@MyStoreActivity).apply {
            setTitle("매장 삭제")
            setMessage("매장을 삭제 하시겠습니까?\n모든 매장 정보가 삭제 됩니다.")
            setPositiveButton("네") { _, _ ->
                viewModel.deleteMyStore()
            }
            setNegativeButton("아니오") { _, _ -> }
            create()
        }

        builder.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnUpdate -> {
                startActivity(Intent(this@MyStoreActivity, StoreUpdateActivity::class.java))
            }

            R.id.btnDelete -> {
                showDialog()
            }
        }
    }
}