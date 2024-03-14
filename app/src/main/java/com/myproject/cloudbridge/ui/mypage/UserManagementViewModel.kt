package com.myproject.cloudbridge.ui.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import com.kakao.sdk.user.UserApiClient
import com.myproject.cloudbridge.datasource.datastore.MainDataStore
import com.myproject.cloudbridge.datasource.local.entity.UserEntity
import com.myproject.cloudbridge.repository.LocalRepository
import com.myproject.cloudbridge.repository.NetworkRepository
import com.myproject.cloudbridge.utility.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserManagementViewModel: ViewModel() {

    private val db = App.db
    private val localRepository = LocalRepository()
    private val networkRepository = NetworkRepository()

    lateinit var userProfile: StateFlow<List<UserEntity>>

    fun getUserProfile() = viewModelScope.launch(Dispatchers.IO) {
        //userProfile = dbRepository.getUserData().stateIn(viewModelScope)
    }

    fun updateUserProfile(userEntity: UserEntity) = viewModelScope.launch(Dispatchers.IO) {
        localRepository.updateUserData(userEntity)
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
        localRepository.deleteUserData(MainDataStore.getUserId())
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
        localRepository.deleteUserData(MainDataStore.getUserId())

        // Firebase Data Delete
        firebaseReference(0).delete()

        // FirstFlag = false
        MainDataStore.FalseFirstData()
    }

    private suspend fun firebaseReference(flag: Int): DocumentReference =
        db.collection("USER").document(MainDataStore.getUserId())


}