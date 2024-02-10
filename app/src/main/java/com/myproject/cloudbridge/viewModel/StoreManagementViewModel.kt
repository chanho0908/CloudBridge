package com.myproject.cloudbridge.viewModel

import android.graphics.Bitmap
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.cloudbridge.dataStore.MainDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import com.myproject.cloudbridge.util.Constants.Companion.createRequestBody
import android.util.Log
import com.myproject.cloudbridge.db.entity.StoreEntity
import com.myproject.cloudbridge.model.store.AllCrnResponseModel
import com.myproject.cloudbridge.model.store.CrnStateResponseModel
import com.myproject.cloudbridge.model.store.CrnStateRequestModel
import com.myproject.cloudbridge.model.store.ModifyStoreStateSaveModel
import com.myproject.cloudbridge.model.store.MyStoreInfoRequestModel
import com.myproject.cloudbridge.repository.DBRepository
import com.myproject.cloudbridge.repository.NetworkRepository
import com.myproject.cloudbridge.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class StoreManagementViewModel: ViewModel() {
    private lateinit var myStoreInfoRequestModel: MyStoreInfoRequestModel

    private val networkRepository = NetworkRepository()
    private val dbRepository = DBRepository()

    // 나의 매장 정보
    private var _myModifyStoreInfo: MutableStateFlow<ModifyStoreStateSaveModel> = MutableStateFlow(
        ModifyStoreStateSaveModel("", "", "", "", "",))
    val myModifyStoreInfo: StateFlow<ModifyStoreStateSaveModel> get() = _myModifyStoreInfo

    // 사업자 등록번호 상태 조회
    private val _state = MutableStateFlow(CrnStateResponseModel(0, 0, "", emptyList()))
    val state: StateFlow<CrnStateResponseModel> get() = _state

    // 서버에 등록된 모든 매장의 사업자 등록번호
    private val _crnList = MutableStateFlow<List<AllCrnResponseModel>>(emptyList())
    val crnList: StateFlow<List<AllCrnResponseModel>> get() = _crnList

    // 나의 매장 정보
    private var _myStore: MutableStateFlow<StoreEntity> = MutableStateFlow(createFirstData())
    val myStore: StateFlow<StoreEntity> get() = _myStore

    // 서버 작업이 완료됐음을 알리는 flag

    private val _flag = MutableStateFlow(false)
    val flag: StateFlow<Boolean> get() = _flag

    // 나의 사업자 등록번호와 일치하는가 ?

    private val _isEqualCrn = MutableStateFlow(false)
    val isEqualCrn: StateFlow<Boolean> get() = _isEqualCrn

    // 모든 매장 정보
    private var _list = MutableStateFlow<List<StoreEntity>>(emptyList())
    val list: StateFlow<List<StoreEntity>> get() = _list

    // 사업자 등록 번호 상태 조회
    fun getCRNState(bno: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = networkRepository.getCRNState(CrnStateRequestModel(arrayOf(bno)))
            _state.value = response

        }catch (e : Exception){
            Log.d("CPRFragment", "Cause getCPRState: $e")
        }
    }

    // 서버에 있는 모든 사업자 등록 번호를 가져오는 메소드
    fun getCompanyRegistrationNumberList() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = networkRepository.getCompanyRegistrationNumber()
            _crnList.value = response

        }catch (e : Exception){
            Log.d("CPRFragment", "Cause getCRNList: $e")
        }
    }

    fun getMyStoreInfo() = viewModelScope.launch(Dispatchers.IO) {
        try {
            // 1. Datastore에 저장된 사업자 등록번호를 가지고
            MainDataStore.getCrn().collect{ crn->
                // 2. Room에서 나의 매장 정보를 가져와
                val response = dbRepository.getMyStoreInfo(crn)

                // 3. flow로 반환된 데이터를 StateFlow로 변환해 저장
                response.stateIn(viewModelScope).collect{ myStore->
                    _myStore.value = myStore
                }
            }
        }catch (e: Exception){
            Log.d("MyPageViewModel","MyPageViewModel: $e")
        }
    }

    // 메장 정보 등록
     fun registrationStore(imgBody: MultipartBody.Part, storeName:String, ceoName: String, crn: String,
        phone:String, addr: String, lat:String, lng:String, kind:String) = viewModelScope.launch(Dispatchers.IO){
        try {
            // 내부 코드 순차 실행
            MainDataStore.setCrn(crn)

            // 매장 정보를 RequestBody Type으로 변환 후
            // Retrofit으로 전송할 MyStoreInfoRequestModel 객체 생성
            val myStoreInfoRequestModel = MyStoreInfoRequestModel(
                imgBody,
                createRequestBody(storeName),
                createRequestBody(ceoName),
                createRequestBody(crn),
                createRequestBody(phone),
                createRequestBody(addr),
                createRequestBody(lat),
                createRequestBody(lng),
                createRequestBody(kind)
            )

            networkRepository.registrationStore(myStoreInfoRequestModel)
            // flag가 변경 = 네트워크 통신 종료
            _flag.value = true
        }catch (e: Exception){
            Log.d("MyPageViewModel","MyPageViewModel: $e")
        }
    }

    fun updateMyStore(imgBody: MultipartBody.Part? = null, storeName:String, representativeName: String, phone:String,
                        addr: String, lat:String, lng:String, kind:String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            MainDataStore.getCrn().collect{ crn ->

                myStoreInfoRequestModel = if (imgBody != null) {
                    // 매장 정보를 RequestBody Type으로 변환 후
                    // Retrofit으로 전송할 MyStoreInfoRequestModel 객체 생성

                    MyStoreInfoRequestModel(
                        imgBody,
                        createRequestBody(storeName),
                        createRequestBody(representativeName),
                        createRequestBody(crn),
                        createRequestBody(phone),
                        createRequestBody(addr),
                        createRequestBody(lat),
                        createRequestBody(lng),
                        createRequestBody(kind)
                    )
                } else {
                    MyStoreInfoRequestModel(
                        null,
                        createRequestBody(storeName),
                        createRequestBody(representativeName),
                        createRequestBody(crn),
                        createRequestBody(phone),
                        createRequestBody(addr),
                        createRequestBody(lat),
                        createRequestBody(lng),
                        createRequestBody(kind)
                    )
                }
                networkRepository.updateStoreInfo(myStoreInfoRequestModel)
                _flag.value = true
            }
        }catch (e: Exception){
            Log.d("MyPageViewModel","MyPageViewModel: $e")
        }
    }

    // 매장 삭제
    fun deleteMyStore() = viewModelScope.launch(Dispatchers.IO) {
        MainDataStore.getCrn().collect{ crn ->
            // Local DB 삭제
            dbRepository.deleteStoreInfo(crn)

            MainDataStore.setCrn("")

            // Remote DB 삭제
            networkRepository.deleteMyStoreInfo(crn)
            _flag.value = true
        }

    }

    // Room에 서버 매장 데이터 저장
    fun fromServerToRoomSetAllStoreList() = viewModelScope.launch(Dispatchers.IO) {

        try{
            // 서버에서 모든 매장 정보를 가져오는 비동기 작업
            val serverData = networkRepository.getAllStoreInfo()

            // 현재 로컬 데이터를 로드
            val localData = _list.value.toMutableList()

            serverData.forEach { serverEntity ->

                // find
                // 조건을 만족하는지 여부를 반환하는 람다 함수
                // element가 특정 조건을 만족하면 true를 반환
                // 조건을 만족하는 첫 번째 요소를 찾으면 해당 요소가 반환됨
                // 여기선 기존에 존재하던 데이터와 서버에서 가져온 새로운 데이터를 비교해
                // crn이 같은 값이 있다면 그에 해당하는 StoreEntity
                // 즉, Room에 저장된 Data를 가져옵니다.
                val findEntity = localData.find { it.crn == serverEntity.crn }

                val newStoreEntity = StoreEntity(
                    serverEntity.crn,
                    Constants.Base64ToBitmaps(serverEntity.image),
                    serverEntity.storeName,
                    serverEntity.ceoName,
                    serverEntity.contact,
                    serverEntity.address,
                    serverEntity.latitude,
                    serverEntity.longitude,
                    serverEntity.kind,
                )

                if (findEntity != null) {
                    Log.d("MAinViewModelData", "데이터 업데이트 ${newStoreEntity.crn}")
                    // Room 에 이미 있는 데이터면 업데이트
                    dbRepository.updateStoreInfo(newStoreEntity)
                }else {
                    Log.d("MAinViewModelData", "데이터 삽입 ${newStoreEntity.crn}")
                    // Room 에 없는 데이터면 추가
                    dbRepository.insertStoreInfo(newStoreEntity)
                }
            }
        }catch (e : java.lang.Exception){
            Log.d("MainViewModel", "Error Causer! : $e ")
        }
    }

    // 모든 매장 정보
    fun showAllStoreFromRoom() = viewModelScope.launch {
        dbRepository.getAllStoreInfo().stateIn(viewModelScope).collect{ storeEntities->
            _list.value = storeEntities
        }
    }

    fun checkMyCrn(crn: String) = viewModelScope.launch {
        MainDataStore.getCrn().collect{
            _isEqualCrn.value = it == crn
        }
    }

    // 사용자 입력 데이터를 업데이트하는 메서드
    fun updateUserData(modifyData: ModifyStoreStateSaveModel) {
        _myModifyStoreInfo.value = modifyData
    }

    // 상태를 Bundle에 저장하는 메서드
    fun saveState(): Bundle {
        val bundle = Bundle()
        bundle.putString("storeName", _myModifyStoreInfo.value.storeName)
        bundle.putString("ceoName", _myModifyStoreInfo.value.ceoName)
        bundle.putString("contact", _myModifyStoreInfo.value.contact)
        bundle.putString("address", _myModifyStoreInfo.value.address)
        bundle.putString("kind", _myModifyStoreInfo.value.kind)
        return bundle
    }

    // Bundle에서 상태를 복원하는 메서드
    fun restoreState(bundle: Bundle?) {
        bundle?.let {
            val storeName = it.getString("storeName", "")
            val ceoName = it.getString("ceoName", "")
            val contact = it.getString("contact", "")
            val address = it.getString("address", "")
            val kind = it.getString("kind", "")
            _myModifyStoreInfo.value = ModifyStoreStateSaveModel(storeName, ceoName, contact, address, kind)
        }
    }



    private fun createFirstData() = StoreEntity("", Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
        "", "", "", "", "", "", "")
}