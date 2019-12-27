package com.sunragav.home24.di

import android.app.Application
import androidx.room.Room
import com.sunragav.home24.data.contract.LocalRepository
import com.sunragav.home24.localdata.datasource.LocalRepositoryImpl
import com.sunragav.home24.localdata.db.ArticlesDB
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [LocalDataModule.Binders::class])
class LocalDataModule {

    @Module
    interface Binders {

        @Binds
        fun bindsLocalDataSource(
            localDataSourceImpl: LocalRepositoryImpl
        ): LocalRepository

    }

    @Provides
    @Singleton
    fun providesDatabase(
        application: Application
    ) = Room.inMemoryDatabaseBuilder(application.applicationContext, ArticlesDB::class.java)
        .allowMainThreadQueries()
        .build()

    @Provides
    @Singleton
    fun providesArticlesDao(
        articlesDB: ArticlesDB
    ) = articlesDB.getArticlesDao()

    @Provides
    @Singleton
    fun providesRequestDao(
        articlesDB: ArticlesDB
    ) = articlesDB.getRequestsDao()

}
