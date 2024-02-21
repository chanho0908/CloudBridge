package com.myproject.cloudbridge.localDB.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "store_table")
data class StoreEntity (
    @PrimaryKey
    @ColumnInfo(name = "company_registration_number")
    val crn: String,
    @ColumnInfo(name = "store_image")
    val imagePath: String,
    @ColumnInfo(name = "store_name")
    var storeName: String,
    @ColumnInfo(name = "ceo_name")
    val ceoName: String,
    val contact: String,
    val address: String,
    val latitude: String,
    val longitude: String,
    val kind: String
)
