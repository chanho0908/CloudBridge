package com.myproject.cloudbridge.datasource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.myproject.cloudbridge.datasource.local.dao.UserDAO
import com.myproject.cloudbridge.datasource.local.entity.UserEntity

@Database(entities = [UserEntity::class], version = 3)
abstract class UserDatabase: RoomDatabase() {
    abstract fun UserDao(): UserDAO

    companion object{

        @Volatile
        private var INSTANCE: UserDatabase?= null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}