package com.myproject.cloudbridge.repository

import com.myproject.cloudbridge.db.MainDatabase
import com.myproject.cloudbridge.db.entity.StoreEntity
import com.myproject.cloudbridge.db.UserDatabase
import com.myproject.cloudbridge.db.entity.UserEntity
import com.myproject.cloudbridge.util.App

class DBRepository {
    val context = App.context()
    val room = context.let { UserDatabase.getDatabase(it) }
    val mainDB = context.let { MainDatabase.getDatabase(it).storeDao() }

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