package com.myproject.cloudbridge.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.cloudbridge.datasource.datastore.MainDataStore
import com.myproject.cloudbridge.datasource.local.entity.UserEntity
import com.myproject.cloudbridge.model.user.User
import com.myproject.cloudbridge.repository.DBRepository
import com.myproject.cloudbridge.util.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IntroViewModel: ViewModel() {
    val db = App.db
    private val repo = DBRepository()

    private val _first = MutableLiveData<Boolean>()
    val first: LiveData<Boolean>
        get() = _first

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _isJoin = MutableLiveData<Boolean>()
    val isJoin: LiveData<Boolean>
        get() = _isJoin

    private val _isUsingId = MutableLiveData<Boolean>()
    val isUsingId: LiveData<Boolean>
        get() = _isUsingId

    fun setUpFirstFlag() = viewModelScope.launch {
        MainDataStore.setupFirstData()
    }

    fun setUserID(userID: String) = viewModelScope.launch {
        MainDataStore.setUserID(userID)
    }

    fun isJoinUser(userKakaoEmail: String) = viewModelScope.launch(Dispatchers.IO) {
        db.collection("USER").get().addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("IntroViewModel", document.data["userKakaoEmail"].toString())
                    if (userKakaoEmail == document.data["userKakaoEmail"].toString()) {
                        _isJoin.value = true
                        break
                    }else{
                        _isJoin.value = false
                    }
                }
        }
    }

    fun isUsingId(userId: String) = viewModelScope.launch(Dispatchers.IO) {
        db.collection("USER").get().addOnSuccessListener { documents ->
            for (document in documents) {
                if (userId == document.data["userID"].toString()) {
                    _isUsingId.value = true
                    break
                }else{
                    _isUsingId.value = false
                }
            }
        }
    }

    //  main thread 에서 DB 접근이 불가능
    fun writeNewUser(user: User) = viewModelScope.launch(Dispatchers.IO){
        val userEntity = UserEntity(
            user.userKakaoEmail.toString(),
            user.userID.toString(),
            user.userPw.toString(),
            user.userName.toString(),
            user.userPhone.toString()
        )

        // 회원 가입 정보 Firebase 저장
        db.collection("USER").document(user.userID.toString()).set(user)

        // 회원 가입 정보 Room 저장
        repo.insertUserData(userEntity)
    }

     fun findUserData(userId: String) = viewModelScope.launch(Dispatchers.IO){

         db.collection("USER").document(userId).get().addOnSuccessListener { document ->
                _user.postValue(document.toObject(User::class.java))
        }.addOnFailureListener { exception->
            Log.d("IntroViewModel", "get fail with $exception")
        }
    }

    fun signUpUser(userEntity: UserEntity) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertUserData(userEntity)
    }

    fun checkFistFlag() = viewModelScope.launch {
        delay(2000)
        val getData = MainDataStore.getFirstFlag()

        _first.value = getData
    }
}