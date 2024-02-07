package com.myproject.cloudbridge.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.cloudbridge.dataStore.MyDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.myproject.cloudbridge.util.Constants.Companion.absolutelyPath
import com.myproject.cloudbridge.util.Constants.Companion.createRequestBody
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.myproject.cloudbridge.model.store.AllCrnResponseModel
import com.myproject.cloudbridge.model.store.CrnStateResponseModel
import com.myproject.cloudbridge.model.store.CrnStateRequestModel
import com.myproject.cloudbridge.model.store.MyStoreInfoRequestModel
import com.myproject.cloudbridge.repository.DBRepository
import com.myproject.cloudbridge.repository.NetworkRepository
import com.myproject.cloudbridge.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//AndroidViewModel() application context 를 사용할 수 있게 만든 viweModel
class StoreManagementViewModel: ViewModel() {
    private lateinit var myStoreInfoRequestModel: MyStoreInfoRequestModel

    private val networkRepository = NetworkRepository()
    private val dbRepository = DBRepository()
    private val dataStore = MyDataStore()

    private val _state = MutableStateFlow(CrnStateResponseModel(0, 0, "", emptyList()))
    val state: StateFlow<CrnStateResponseModel> get() = _state

    private val _crnList = MutableStateFlow<List<AllCrnResponseModel>>(emptyList())
    val crnList: StateFlow<List<AllCrnResponseModel>> get() = _crnList

    private val _flag = MutableLiveData<Unit>()
    val flag: LiveData<Unit> get() = _flag

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

    // 메장 정보 등록
    // viewModel은 context 를 가지면 안된다 -> memory leak 발생 여지
    // a b -> Z Viewmodel 다른 액티비티의 context를 가지고 있을 경우 발생
    // 예외 : 앱의 컨텍스트 Aplication Context <- 뷰모델보다 오래살아있음
     fun registrationStore(context: Context, imgUri: Uri, storeName:String, ceoName: String, crn: String,
        phone:String, addr: String, lat:String, lng:String, kind:String) = viewModelScope.launch(Dispatchers.IO){

            // 내부 코드 순차 실행
            MyDataStore().setCrn(crn)

             // 이미지 절대경로 반환 후 파일 객체 생성
             val file = File(absolutelyPath(imgUri, context))

             // 파일 객체를 RequestBody Type으로 변환
             val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

             // MultipartBody.Part Type으로 변환
             val imgBody = MultipartBody.Part.createFormData("storeMainImage", file.name, requestFile)

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
             // flag가 변경 = 네트워크 통신이 종료되었다
            _flag.postValue(Unit)
    }

    fun updateMyStore(context: Context, imgUri: Uri? = null, storeName:String, representativeName: String, phone:String,
                        addr: String, lat:String, lng:String, kind:String)
    = viewModelScope.launch(Dispatchers.IO) {

        dataStore.getCrn().collect{ crn ->

            myStoreInfoRequestModel = if (imgUri != null) {
                val imgBody = Constants.createMultipartBodyPart(imgUri, "storeImage", context)
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
        }
        networkRepository.updateStoreInfo(myStoreInfoRequestModel)
    }

    // 매장 삭제
    fun deleteMyStore() = viewModelScope.launch(Dispatchers.IO) {
        dataStore.getCrn().collect{ crn ->
            // Local DB 삭제
            dbRepository.deleteStoreInfo(crn)

            dataStore.setCrn("")

            // Remote DB 삭제
            networkRepository.deleteMyStoreInfo(crn)

        }

    }
}