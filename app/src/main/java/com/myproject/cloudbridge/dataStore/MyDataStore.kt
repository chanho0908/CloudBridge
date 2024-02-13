package com.myproject.cloudbridge.dataStore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.myproject.cloudbridge.util.App
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.IOException

object MainDataStore {
    private fun getContext(): Context? = App.context()

    private val mDataStore: DataStore<Preferences>?
        get() = getContext()?.dataStore

    private val Context.dataStore : DataStore<Preferences> by preferencesDataStore("user_pref")
    private val FIRST_FLAG = booleanPreferencesKey("FIRST_FLAG")
    private val USER_ID = stringPreferencesKey("USER_ID")
    private val CRN = stringPreferencesKey("CRN")

    /**
     * DataStore의 값을 쓸때는 edit()
     * DataStore에 값을 작성하기 위해서는 반드시 비동기로 동작해야하기에 suspend를 이용
     * */
    suspend fun setupFirstData(){
        mDataStore?.edit { pref ->
            pref[FIRST_FLAG] = true
        }
    }

    suspend fun FalseFirstData(){
        mDataStore?.edit { pref ->
            pref[FIRST_FLAG] = false
        }
    }

    suspend fun getFirstFlag(): Boolean{
        var currentValue = false

        mDataStore?.edit { pref->
            currentValue = pref[FIRST_FLAG] ?: false
        }

        return currentValue
    }

    suspend fun setUserID(userID: String){
        mDataStore?.edit { pref->
            pref[USER_ID] = userID
        }
    }

    suspend fun getUserId(): String{
        var userID = ""

        mDataStore?.edit { pref->
            userID = pref[USER_ID] ?: "default Value"
        }

        return userID
    }

    suspend fun setCrn(crn: String){
        mDataStore?.edit {pref->
            pref[CRN] = crn
        }
    }

    // 1.완전히 전역적으로 crn을 등록
    fun getCrn(): Flow<String>? {
        return mDataStore?.data?.map { preferences ->
            preferences[CRN] ?: ""
        }
    }
}