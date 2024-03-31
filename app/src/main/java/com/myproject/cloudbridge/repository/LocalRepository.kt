package com.myproject.cloudbridge.repository

import com.myproject.cloudbridge.datasource.local.MainDatabase
import com.myproject.cloudbridge.datasource.local.entity.StoreEntity
import com.myproject.cloudbridge.datasource.local.UserDatabase
import com.myproject.cloudbridge.datasource.local.entity.RecentlySearchKeywordEntity
import com.myproject.cloudbridge.datasource.local.entity.UserEntity
import com.myproject.cloudbridge.utility.App

class LocalRepository {
    private val context = App.context()
    private val userDatabase = UserDatabase.getDatabase(context)
    private val storeDatabase =  MainDatabase.getDatabase(context).storeInfoDao()

    fun getUserData() = userDatabase.UserDao().readUserData()

    suspend fun insertUserData(userEntity: UserEntity) = userDatabase.UserDao().insertUserData(userEntity)

    suspend fun updateUserData(userEntity: UserEntity) = userDatabase.UserDao().updateUserData(userEntity)

    suspend fun deleteUserData(userId: String) = userDatabase.UserDao().deleteUserData(userId)

    fun readAllKeyword() = userDatabase.RecentlySearchKeywordDAO().readAllKeyword()

    suspend fun insertKeyword(keyword: RecentlySearchKeywordEntity) = userDatabase.RecentlySearchKeywordDAO().insertKeyword(keyword)

    suspend fun deleteKeyword(id: Long) = userDatabase.RecentlySearchKeywordDAO().deleteKeyword(id)

    suspend fun deleteAllKeyword() = userDatabase.RecentlySearchKeywordDAO().deleteAllKeyword()

    suspend fun insertStoreInfo(storeEntity: StoreEntity) = storeDatabase.insert(storeEntity)

    fun getAllStoreInfo() = storeDatabase.readAllStoreInfo()

    fun getMyStoreInfo(crn: String) = storeDatabase.readMyStoreInfo(crn)

    fun getAutoCompleteSearchResult(query: String) = storeDatabase.readAutoCompleteSearchKeyword(query)

    suspend fun updateStoreInfo(storeEntity: StoreEntity) = storeDatabase.update(storeEntity)

    suspend fun deleteStoreInfo(crn: String) = storeDatabase.deleteStoreData(crn)
}