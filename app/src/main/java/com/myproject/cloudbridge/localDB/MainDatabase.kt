package com.myproject.cloudbridge.localDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.myproject.cloudbridge.localDB.dao.StoreInfoDAO
import com.myproject.cloudbridge.localDB.entity.StoreEntity
import com.myproject.cloudbridge.localDB.typeConverter.ImageTypeConverter

@Database(entities = [StoreEntity::class], version=4)
@TypeConverters(ImageTypeConverter::class)
abstract class MainDatabase: RoomDatabase() {
    abstract fun storeInfoDao(): StoreInfoDAO

    companion object{

        @Volatile
        private var INSTANCE: MainDatabase ?= null

        fun getDatabase(context: Context): MainDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDatabase::class.java,
                    "main_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}