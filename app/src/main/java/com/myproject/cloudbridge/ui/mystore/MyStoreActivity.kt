package com.myproject.cloudbridge.ui.mystore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.ActivityMyStoreBinding
import com.myproject.cloudbridge.model.store.StoreInfoSettingModel
import com.myproject.cloudbridge.repository.LocalRepository
import com.myproject.cloudbridge.repository.NetworkRepository
import com.myproject.cloudbridge.ui.main.MainActivity
import com.myproject.cloudbridge.ui.mystore.vm.StoreManagementViewModel
import com.myproject.cloudbridge.ui.mystore.vm.StoreManagementViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyStoreActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMyStoreBinding
    private lateinit var viewModel: StoreManagementViewModel
    private lateinit var viewModelFactory: StoreManagementViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        initViewModel()
        initToolbar()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.imgLoading.collect { loading ->
                    if (loading == true) {
                        viewModel.myStore.collect { store ->
                            with(binding) {
                                storeCeoNameTextView.text = store.storeInfo.ceoName
                                storeNameTextView.text = store.storeInfo.storeName
                                storePhoneTextView.text = store.storeInfo.contact
                                storeAddrTextView.text = store.storeInfo.address
                                storeKindTextView.text = store.storeInfo.kind

                                Glide.with(this@MyStoreActivity)
                                    .load(store.storeImage)
                                    .into(storeMainImage)

                                btnUpdate.setOnClickListener(this@MyStoreActivity)
                                btnDelete.setOnClickListener(this@MyStoreActivity)
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.flag.collect {
                    if (it == true) {
                        val intent = Intent(this@MyStoreActivity, NoRegisteredStoreActivity::class.java)
                        intent.putExtra("FLAG", "DELETE")
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {  }
    }

    private fun initViewModel(){
        viewModelFactory = StoreManagementViewModelFactory(NetworkRepository(), LocalRepository())
        viewModel = ViewModelProvider(this, viewModelFactory)[StoreManagementViewModel::class.java]
        viewModel.getMyStoreInfo()
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