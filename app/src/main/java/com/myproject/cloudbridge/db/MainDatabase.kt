package com.myproject.cloudbridge.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.myproject.cloudbridge.db.dao.StoreDao
import com.myproject.cloudbridge.db.entity.StoreEntity
import com.myproject.cloudbridge.db.typeConverter.ImageTypeConverter

@Database(entities = [StoreEntity::class], version=1)
@TypeConverters(ImageTypeConverter::class)
abstract class MainDatabase: RoomDatabase() {
    abstract fun storeDao(): StoreDao

    companion object{

        @Volatile
        private var INSTANCE: MainDatabase ?= null

        fun getDatabase(context: Context): MainDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDatabase::class.java,
                    "main_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}