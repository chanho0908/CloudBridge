package com.myproject.cloudbridge.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.myproject.cloudbridge.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {

    // Flow 사용시 UI에서 보여주는 Data를 실시간으로 보여줄 수 있음!!
    // Return 타입이 Flow일 때는 suspend 사용 불가능!
    @Query("SELECT * FROM user_table")
    fun readUserData(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserData(user: UserEntity)

    @Update
    suspend fun updateUserData(user: UserEntity)

    // Delete
    @Query("DELETE FROM user_table WHERE userID = :userId")
    suspend fun deleteUserData(userId: String)


}