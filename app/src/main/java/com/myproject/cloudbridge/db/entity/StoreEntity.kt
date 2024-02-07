package com.myproject.cloudbridge.db.entity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

@Entity(tableName = "store_table")
data class StoreEntity (
    @PrimaryKey
    @ColumnInfo(name = "company_registration_number")
    val crn: String,
    @ColumnInfo(name = "store_image")
    val image: Bitmap,
    @ColumnInfo(name = "store_name")
    val storeName: String,
    @ColumnInfo(name = "ceo_name")
    val ceoName: String,
    val contact: String,
    val address: String,
    val latitude: String,
    val longitude: String,
    val kind: String
)
