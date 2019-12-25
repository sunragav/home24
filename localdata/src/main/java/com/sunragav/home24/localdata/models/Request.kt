package com.sunragav.home24.localdata.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "request")
data class Request(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int=0,
    @ColumnInfo(name = "offset") val offset: Int,
    @ColumnInfo(name = "limit") val limit: Int
)