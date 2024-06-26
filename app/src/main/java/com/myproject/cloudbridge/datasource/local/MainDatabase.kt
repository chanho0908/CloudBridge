package com.myproject.cloudbridge.datasource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.myproject.cloudbridge.datasource.local.dao.StoreInfoDAO
import com.myproject.cloudbridge.datasource.local.entity.StoreEntity
import com.myproject.cloudbridge.datasource.local.typeConverter.ImageTypeConverter

@Database(entities = [StoreEntity::class], version=5)
@TypeConverters(ImageTypeConverter::class)
abstract class MainDatabase: RoomDatabase() {
    abstract fun storeInfoDao(): StoreInfoDAO

    companion object{

        @Volatile
        private var INSTANCE: MainDatabase?= null

        fun getDatabase(context: Context): MainDatabase {
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