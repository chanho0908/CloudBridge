package com.myproject.cloudbridge.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_search_keyword_table")
data class RecentlySearchKeywordEntity (
    val keyword: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
