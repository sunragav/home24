package com.sunragav.home24.localdata.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleLocalData(
    @PrimaryKey @ColumnInfo(name = "sku") val sku: String,
    @ColumnInfo(name = "imageUrl") val imageUrl: String,
    @ColumnInfo(name = "is_flagged") val flagged: Boolean,
    @ColumnInfo(name = "title") val title: String
)