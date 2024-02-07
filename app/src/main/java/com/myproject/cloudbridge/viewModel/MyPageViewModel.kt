package com.myproject.cloudbridge.viewModel

import com.myproject.cloudbridge.util.Constants.Companion.createMultipartBodyPart
import com.myproject.cloudbridge.util.Constants.Companion.createRequestBody
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.cloudbridge.dataStore.MyDataStore
import com.myproject.cloudbridge.db.entity.StoreEntity
import com.myproject.cloudbridge.model.store.MyStoreInfoRequestModel
import com.myproject.cloudbridge.repository.DBRepository
import com.myproject.cloudbridge.repository.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyPageViewModel: ViewModel() {
    private val dbRepository = DBRepository()
    private val dataStore = MyDataStore()

    private var _myStore: MutableStateFlow<StoreEntity> = MutableStateFlow(createFirstData())
    val myStore: StateFlow<StoreEntity> get() = _myStore

    private val _isEqualCrn = MutableStateFlow(true)
    val isEqualCrn: StateFlow<Boolean> get() = _isEqualCrn

    fun getMyStoreInfo() = viewModelScope.launch(Dispatchers.IO) {
        try {
            // 1. Datastore에 저장된 사업자 등록번호를 가지고
            dataStore.getCrn().collect{ crn->
                Log.d("dasdsadsadas", "MyPageViewModel / getMyStoreInfo crn : $crn")
                // 2. Room에서 나의 매장 정보를 가져와
                val response = dbRepository.getMyStoreInfo(crn)

                // 3. flow로 반환된 데이터를 StateFlow로 변환해 저장
                response.stateIn(viewModelScope).collect{ myStore->
                    Log.d("dasdsadsadas", "MyPageViewModel / getMyStoreInfo myStore : $myStore")
                    _myStore.value = myStore
                }
            }
        }catch (e: Exception){
            Log.d("MyPageViewModel","MyPageViewModel: $e")
        }
    }

    fun checkMyCrn(crn: String) = viewModelScope.launch(Dispatchers.IO) {
        dataStore.getCrn().collect{
            Log.d("sadssa", "${it} vs ${crn}")
            _isEqualCrn.value = it == crn
        }
    }

    private fun createFirstData() = StoreEntity("", Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
        "", "", "", "", "", "", "")
}