package com.myproject.cloudbridge.repository

import com.myproject.cloudbridge.localDB.MainDatabase
import com.myproject.cloudbridge.localDB.entity.StoreEntity
import com.myproject.cloudbridge.localDB.UserDatabase
import com.myproject.cloudbridge.localDB.entity.UserEntity
import com.myproject.cloudbridge.util.App

class DBRepository {
    private val context = App.context()
    private val room = UserDatabase.getDatabase(context)
    private val mainDB =  MainDatabase.getDatabase(context).storeInfoDao()

    fun getUserData() = room.UserDao().readUserData() // flow

    suspend fun insertUserData(userEntity: UserEntity) = room.UserDao().insertUserData(userEntity)

    suspend fun updateUserData(userEntity: UserEntity) = room.UserDao().updateUserData(userEntity)

    suspend fun deleteUserData(userId: String) = room.UserDao().deleteUserData(userId)

    //////////////////////////////////////////////////////////////////////////////////////////////

    suspend fun insertStoreInfo(storeEntity: StoreEntity) = mainDB.insert(storeEntity)

    fun getAllStoreInfo() = mainDB.readAllStoreInfo()

    fun getMyStoreInfo(crn: String) = mainDB.readMyStoreInfo(crn)

    suspend fun updateStoreInfo(storeEntity: StoreEntity) = mainDB.update(storeEntity)

    suspend fun deleteStoreInfo(crn: String) = mainDB.deleteStoreData(crn)
}