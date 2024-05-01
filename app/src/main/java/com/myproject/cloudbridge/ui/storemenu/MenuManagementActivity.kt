package com.myproject.cloudbridge.ui.storemenu


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.myproject.cloudbridge.databinding.ActivityMenuManagementBinding
import com.myproject.cloudbridge.repository.NetworkRepository
import com.myproject.cloudbridge.ui.storemenu.vm.StoreMenuManagementViewModel
import com.myproject.cloudbridge.ui.storemenu.vm.StoreMenuManagementViewModelFactory

class MenuManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuManagementBinding

    lateinit var viewModel: StoreMenuManagementViewModel
    private lateinit var viewModelFactory: StoreMenuManagementViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
    }

    private fun initViewModel(){
        viewModelFactory = StoreMenuManagementViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory)[StoreMenuManagementViewModel::class.java]
    }


//    private fun initRv(){
//        val newItem = StoreMenuRvModel(null, "", 0, "")
//
//        // 데이터 리스트에 항목 추가
//        menuList.add(newItem)
//
//        menuAdapter = MenuRvAdapter(
//            this,
//            menuList,
//            { position ->
//                if (hasImagePermission()){ accessGallery(launcherForActivity) }
//                else launcherForPermission.launch(REQUEST_IMAGE_PERMISSIONS)
//                selectedItemPosition = position
//            }
//        ) { position ->
//            menuList.removeAt(position)
//
//            menuAdapter.notifyItemRemoved(position)
//            menuAdapter.notifyItemRangeChanged(position, menuList.size)
//        }
//
//        with(binding.rv){
//            adapter = menuAdapter
//            layoutManager = LinearLayoutManager(this@MenuManagementActivity)
//        }
//    }
//
//    private fun addMenuItem() {
//        // 새로운 항목 생성
//        val newItem = StoreMenuRvModel(null, "", 0, "")
//
//        // 데이터 리스트에 항목 추가
//        menuList.add(newItem)
//
//        // 새로운 항목이 추가되었음을 어댑터에 알림
//        menuAdapter.notifyItemInserted(menuList.size - 1)
//        // 스크롤을 추가된 항목으로 이동시킴
//        binding.rv.scrollToPosition(menuList.size - 1)
//    }

}

