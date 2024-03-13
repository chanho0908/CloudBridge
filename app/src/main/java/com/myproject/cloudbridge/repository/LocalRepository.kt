package com.myproject.cloudbridge.repository

import com.myproject.cloudbridge.datasource.local.MainDatabase
import com.myproject.cloudbridge.datasource.local.entity.StoreEntity
import com.myproject.cloudbridge.datasource.local.UserDatabase
import com.myproject.cloudbridge.datasource.local.entity.RecentlySearchKeywordEntity
import com.myproject.cloudbridge.datasource.local.entity.UserEntity
import com.myproject.cloudbridge.util.App

class LocalRepository {
    private val context = App.context()
    private val userDatabase = UserDatabase.getDatabase(context)
    private val mainDB =  MainDatabase.getDatabase(context).storeInfoDao()

    fun getUserData() = userDatabase.UserDao().readUserData()

    suspend fun insertUserData(userEntity: UserEntity) = userDatabase.UserDao().insertUserData(userEntity)

    suspend fun updateUserData(userEntity: UserEntity) = userDatabase.UserDao().updateUserData(userEntity)

    suspend fun deleteUserData(userId: String) = userDatabase.UserDao().deleteUserData(userId)

    fun readAllKeyword() = userDatabase.RecentlySearchKeywordDAO().readAllKeyword()

    suspend fun insertKeyword(keyword: RecentlySearchKeywordEntity) = userDatabase.RecentlySearchKeywordDAO().insertKeyword(keyword)

    suspend fun deleteKeyword(keyword: String) = userDatabase.RecentlySearchKeywordDAO().deleteKeyword(keyword)

    suspend fun deleteAllKeyword() = userDatabase.RecentlySearchKeywordDAO().deleteAllKeyword()

    suspend fun insertStoreInfo(storeEntity: StoreEntity) = mainDB.insert(storeEntity)

    fun getAllStoreInfo() = mainDB.readAllStoreInfo()

    fun getMyStoreInfo(crn: String) = mainDB.readMyStoreInfo(crn)

    suspend fun updateStoreInfo(storeEntity: StoreEntity) = mainDB.update(storeEntity)

    suspend fun deleteStoreInfo(crn: String) = mainDB.deleteStoreData(crn)
}