package com.sunragav.home24.di

import android.app.Application
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
    ) = ArticlesDB.getInstance(application.applicationContext)

    @Provides
    @Singleton
    fun providesArticlesDao(
        articlesDB: ArticlesDB
    ) = articlesDB.getArticlesDao()

}
