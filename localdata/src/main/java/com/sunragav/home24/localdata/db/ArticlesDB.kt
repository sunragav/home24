package com.sunragav.home24.localdata.db

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sunragav.home24.localdata.dao.ArticlesDao
import com.sunragav.home24.localdata.models.ArticleLocalData

@Database(
    entities = [ArticleLocalData::class],
    version = 1,
    exportSchema = false
)
abstract class ArticlesDB : RoomDatabase() {

    companion object {
        private val LOCK = Any()
        private const val DATABASE_NAME = "home24.db"
        @Volatile
        private var INSTANCE: ArticlesDB? = null

        fun getInstance(@NonNull context: Context): ArticlesDB {
            if (INSTANCE == null) {
                synchronized(LOCK) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context,
                            ArticlesDB::class.java,
                            DATABASE_NAME
                        ).build()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    abstract fun getArticlesDao(): ArticlesDao

}

