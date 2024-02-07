package com.myproject.cloudbridge.viewModel

import com.myproject.cloudbridge.util.Constants.Companion.Base64ToBitmaps
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import com.kakao.sdk.user.UserApiClient
import com.myproject.cloudbridge.dataStore.MyDataStore
import com.myproject.cloudbridge.db.entity.StoreEntity
import com.myproject.cloudbridge.db.entity.UserEntity
import com.myproject.cloudbridge.repository.DBRepository
import com.myproject.cloudbridge.repository.NetworkRepository
import com.myproject.cloudbridge.util.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel: ViewModel() {

    private val db = App.db
    private val dbRepository = DBRepository()
    private val networkRepository = NetworkRepository()

    lateinit var userProfile: StateFlow<List<UserEntity>>

    private var _list = MutableStateFlow<List<StoreEntity>>(emptyList())
    val list: StateFlow<List<StoreEntity>> get() = _list

    //Room에 서버 매장 데이터 저장
    fun fromServerToRoomSetAllStoreList() = viewModelScope.launch(Dispatchers.IO) {

        try{
            // 서버에서 모든 매장 정보를 가져오는 비동기 작업
            val serverData = networkRepository.getAllStoreInfo()

            // 현재 로컬 데이터를 로드
            val localData = _list.value.toMutableList()

            serverData.forEach { serverEntity ->
                Log.d("MAinViewModelData", "serverEntity : $serverEntity")
                // find
                // 조건을 만족하는지 여부를 반환하는 람다 함수
                // element가 특정 조건을 만족하면 true를 반환
                // 조건을 만족하는 첫 번째 요소를 찾으면 해당 요소가 반환됨
                // 여기선 기존에 존재하던 데이터와 서버에서 가져온 새로운 데이터를 비교해
                // crn이 같은 값이 있다면 그에 해당하는 StoreEntity
                // 즉, Room에 저장된 Data를 가져옵니다.
                val findEntity = localData.find { it.crn == serverEntity.crn }
                Log.d("MAinViewModelData", "findEntity : $findEntity")

                val newStoreEntity = StoreEntity(
                    serverEntity.crn,
                    Base64ToBitmaps(serverEntity.image),
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
        }catch (e : Exception){
            Log.d("MainViewModel", "Error Causer! : $e ")
        }
    }

    fun showAllStoreFromRoom() = viewModelScope.launch {
        dbRepository.getAllStoreInfo().stateIn(viewModelScope).collect{ storeEntities->
            _list.value = storeEntities
        }
    }

    fun getUserProfile() = viewModelScope.launch(Dispatchers.IO) {
        userProfile = dbRepository.getUserData().stateIn(viewModelScope)
    }

    fun updateUserProfile(userEntity: UserEntity) = viewModelScope.launch(Dispatchers.IO) {
        dbRepository.updateUserData(userEntity)
        firebaseReference(0).update(
            mapOf(
                "userName" to userEntity.userName,
                "userPhone" to userEntity.userPhone,
                "userPw" to userEntity.userPw
            ))

    }

    fun logout() = viewModelScope.launch(Dispatchers.IO) {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("MainViewModel", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
            }
            else {
                Log.i("MainViewModel", "로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }
        dbRepository.deleteUserData(MyDataStore().getUserId())
    }

    fun deleteUser() = viewModelScope.launch(Dispatchers.IO) {
        // 카카오 로그인 정보 삭제
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.d("MainViewModel", "회원 탈퇴 실패 $error")
            }else {
                Log.d("MainViewModel", "회원 탈퇴 성공 $error")
            }
        }

        // Room Data Delete
        dbRepository.deleteUserData(MyDataStore().getUserId())

        // Firebase Data Delete
        firebaseReference(0).delete()

        // FirstFlag = false
        MyDataStore().FalseFirstData()
    }

    private suspend fun firebaseReference(flag: Int): DocumentReference =
        db.collection("USER").document(MyDataStore().getUserId())


}