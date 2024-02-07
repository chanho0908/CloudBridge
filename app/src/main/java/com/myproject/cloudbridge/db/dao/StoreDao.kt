package com.myproject.cloudbridge.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.myproject.cloudbridge.db.entity.StoreEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface StoreDao {
    @Query("SELECT * FROM store_table")
    fun readAllStoreInfo() : Flow<List<StoreEntity>>

    @Query("SELECT * FROM store_table WHERE company_registration_number = :crn")
    fun readMyStoreInfo(crn: String) : Flow<StoreEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(store: StoreEntity)

    @Update
    suspend fun update(store: StoreEntity)

    // Delete
    @Query("DELETE FROM store_table WHERE company_registration_number = :crn")
    suspend fun deleteStoreData(crn: String)
}