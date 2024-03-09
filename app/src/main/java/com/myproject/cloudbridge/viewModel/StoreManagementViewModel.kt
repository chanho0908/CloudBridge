package com.myproject.cloudbridge.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.cloudbridge.dataStore.MainDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.myproject.cloudbridge.localDB.entity.StoreEntity
import com.myproject.cloudbridge.model.store.AllCrnResponseModel
import com.myproject.cloudbridge.model.store.CrnStateResponseModel
import com.myproject.cloudbridge.model.store.CrnStateRequestModel
import com.myproject.cloudbridge.model.store.MyStoreInfoRequestModel
import com.myproject.cloudbridge.model.store.StoreInfoSettingModel
import com.myproject.cloudbridge.repository.DBRepository
import com.myproject.cloudbridge.repository.NetworkRepository
import com.myproject.cloudbridge.util.singleton.Utils.Base64ToBitmaps
import com.myproject.cloudbridge.util.singleton.Utils.createRequestBody
import com.myproject.cloudbridge.util.singleton.Utils.makeStoreMainImage
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class StoreManagementViewModel : ViewModel() {
    private lateinit var myStoreInfoRequestModel: MyStoreInfoRequestModel

    private val networkRepository = NetworkRepository()
    private val dbRepository = DBRepository()
    init {
        Log.d("MapFragmentLifeCycle", "viewModel Create")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("MapFragmentLifeCycle", "viewModel onCleared")
    }

    // 사업자 등록번호 상태 조회
    private val _state = MutableStateFlow(CrnStateResponseModel(0, 0, "", emptyList()))
    val state = _state.asStateFlow()

    // 서버에 등록된 모든 매장의 사업자 등록번호
    private val _crnList = MutableStateFlow<List<AllCrnResponseModel>>(emptyList())
    val crnList = _crnList.asStateFlow()

    // 나의 매장 정보
    private var _myStore: MutableStateFlow<StoreInfoSettingModel> = MutableStateFlow(initStoreData())
    val myStore = _myStore.asStateFlow()

    // 나의 사업자 등록번호와 일치하는가 ?
    // false, true 일 경우 타입 추론이 가능하다
    // null일 경우 타입을 명시해줘야 한다.
    private val _isEqualCrn = MutableStateFlow<Boolean?>(null)
    val isEqualCrn = _isEqualCrn.asStateFlow()

    // 모든 매장 정보
    private var _allStoreData = MutableStateFlow<ArrayList<StoreInfoSettingModel>?>(null)
    val allStoreData = _allStoreData.asStateFlow()

    // 내 사업자 등록 번호
    private var _myCompanyRegistrationNumber = MutableStateFlow<String?>(null)
    val myCompanyRegistrationNumber = _myCompanyRegistrationNumber.asStateFlow()

    // 서버 작업이 완료 됐음을 알리는 flag
    private val _flag = MutableStateFlow<Boolean?>(null)
    val flag = _flag.asStateFlow()

    // 서버 이미지 요청 작업이 완료 됐음을 알리는 flag
    private val _imgLoading = MutableStateFlow<Boolean?>(null)
    val imgLoading = _imgLoading.asStateFlow()

    // 로컬 패치 완료 됐음을 알리는 flag
    private val _fetch = MutableStateFlow<Boolean?>(null)
    val fetch = _fetch.asStateFlow()

    init {
        viewModelScope.launch {
            MainDataStore.getCrn().collect {
                _myCompanyRegistrationNumber.value = it
            }
        }

    }

    // 사업자 등록 번호 상태 조회
    fun getCRNState(crn: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = networkRepository.getCRNState(CrnStateRequestModel(arrayOf(crn)))
            _state.value = response
        } catch (e: Exception) {
            Log.d(TAG, "getCRNList: $e")
        }
    }

    // 서버에 있는 모든 사업자 등록 번호를 가져오는 메소드
    fun getCompanyRegistrationNumberList() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = networkRepository.getCompanyRegistrationNumber()
            _crnList.value = response

        } catch (e: Exception) {
            Log.d(TAG, "getCompanyRegistrationNumberList: $e")
        }
    }

    fun getMyStoreInfo() = viewModelScope.launch(Dispatchers.IO) {
        try {
            myCompanyRegistrationNumber.collect{ myCompanyRegistrationNumber->
                val response = dbRepository.getMyStoreInfo(myCompanyRegistrationNumber.toString())
                response.stateIn(viewModelScope).collect {
                    _myStore.value.storeInfo = it
                    requestImageFromServer(it.imagePath)
                }
            }

        } catch (e: Exception) {
            Log.d(TAG, "getMyStoreInfo: $e")
        }
    }

    fun requestImageFromServer(imagePath: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = networkRepository.getMyStoreMainImage(imagePath)
            val decodeImage = Base64ToBitmaps(response)
            _myStore.value.storeImage = decodeImage
            _imgLoading.value = true
        } catch (e: Exception) {
            Log.d(TAG, "requestImageFromServer: $e")
        }
    }

    // 메장 정보 등록
    fun registrationStore(imgUrl: Uri, storeName: String, ceoName: String, crn: String,
                          phone: String, addr: String, lat: String, lng: String, kind: String) = viewModelScope.launch(Dispatchers.IO) {
        try {

            val imgBody = makeStoreMainImage(imgUrl)
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
            fromServerToRoomSetAllStoreList()
            // flag가 변경 = 네트워크 통신 종료
            _flag.value = true
        } catch (e: Exception) {
            Log.d(TAG, "MyPageViewModel: $e")
        }
    }

    fun updateMyStore(imgBody: MultipartBody.Part? = null, storeName: String, ceoName: String, crn: String,
                      phone: String, addr: String, lat: String, lng: String, kind: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            myStoreInfoRequestModel = if (imgBody != null) {
                // 매장 정보를 RequestBody Type으로 변환 후
                // Retrofit으로 전송할 MyStoreInfoRequestModel 객체 생성

                MyStoreInfoRequestModel(
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
            } else {
                MyStoreInfoRequestModel(
                    null,
                    createRequestBody(storeName),
                    createRequestBody(ceoName),
                    createRequestBody(crn),
                    createRequestBody(phone),
                    createRequestBody(addr),
                    createRequestBody(lat),
                    createRequestBody(lng),
                    createRequestBody(kind)
                )
            }
            val updateStoreInfoDeferred = async(Dispatchers.IO) {
                networkRepository.updateStoreInfo(myStoreInfoRequestModel)
            }
            updateStoreInfoDeferred.await()
            fromServerToRoomSetAllStoreList()
        } catch (e: Exception) {
            Log.d(TAG, "Cause updateMyStore: $e")
        }
    }

    // 매장 삭제
    fun deleteMyStore() = viewModelScope.launch(Dispatchers.IO) {
        Log.d(TAG, "Delete Request")
        try{
            myCompanyRegistrationNumber.collect {
                // Local DB 삭제
                if (it != null) {
                    dbRepository.deleteStoreInfo(it)
                    MainDataStore.setCrn("")

                    // Remote DB 삭제
                    networkRepository.deleteMyStoreInfo(it)
                    _flag.value = true
                }
            }
        }catch (e: Exception){
            Log.d(TAG, "deleteMyStore cause Error : $e")
        }
    }

    // Room에 서버 매장 데이터 저장
    fun fromServerToRoomSetAllStoreList() = viewModelScope.launch(Dispatchers.IO) {
        try {
            dbRepository.getAllStoreInfo().stateIn(viewModelScope).collect { storeEntities ->

                // 서버에서 모든 매장 정보를 가져 오는 비동기 작업
                val serverData = networkRepository.getAllStoreInfo()
                // 현재 로컬 데이터를 로드
                val localData = storeEntities.toMutableList()

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
                        serverEntity.imagePath,
                        serverEntity.storeName,
                        serverEntity.ceoName,
                        serverEntity.contact,
                        serverEntity.address,
                        serverEntity.latitude,
                        serverEntity.longitude,
                        serverEntity.kind,
                    )

                    if (findEntity != null) {
                        // Room 에 이미 있는 데이터면 업데이트
                        dbRepository.updateStoreInfo(newStoreEntity)
                    } else {
                        // Room 에 없는 데이터면 추가
                        dbRepository.insertStoreInfo(newStoreEntity)
                    }
                }
                _flag.value = true
            }

        } catch (e: java.lang.Exception) {
            Log.d(TAG, "Cause fromServerToRoomSetAllStoreList : $e ")
        }
    }

    // 모든 매장 정보
    fun fetchAllStoreFromRoom() = viewModelScope.launch {
        fromServerToRoomSetAllStoreList()
        val allStoreData =  ArrayList<StoreInfoSettingModel>()

        dbRepository.getAllStoreInfo().stateIn(viewModelScope).collect { storeEntities ->

            storeEntities. forEach { storeEntity ->
                val imagePath = storeEntity.imagePath
                val imgBase64 = networkRepository.getMyStoreMainImage(imagePath)
                val decodedImg = Base64ToBitmaps(imgBase64)
                allStoreData.add(StoreInfoSettingModel(storeEntity, decodedImg))
            }
            Log.d("MapFragmentLifeCycle", "fetchAllStoreFromRoom() ${allStoreData}")
            _allStoreData.value = (allStoreData)
            _fetch.value = true
        }

    }

    fun checkMyCompanyRegistrationNumber(crn: String) = viewModelScope.launch {
        myCompanyRegistrationNumber.collect {
            _isEqualCrn.value = it == crn
        }
    }

    // 사용자 수정 데이터 업데이트
    fun updateSavedData(storeName: String, ceoName: String, contact: String, address: String, kind: String) {
        // 바뀌지 않은 값은 유지
        _myStore.value.storeInfo = _myStore.value.storeInfo.copy(
            storeName = storeName,
            contact = contact,
            ceoName = ceoName,
            address = address,
            kind = kind
        )
    }

    private fun initStoreData() = StoreInfoSettingModel(
        StoreEntity("", "", "", "", "", "", "", "", ""),
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    )

    companion object {
        const val TAG = "StoreManagementViewModel"
    }
}