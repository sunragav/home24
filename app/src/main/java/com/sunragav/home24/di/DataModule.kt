package com.sunragav.home24.di

import com.sunragav.home24.data.ArticlesRepositoryImpl
import com.sunragav.home24.domain.repository.ArticlesRepository
import dagger.Binds
import dagger.Module

@Module
abstract class DataModule {
    @Binds
    abstract fun bindsRepository(
        repoImpl: ArticlesRepositoryImpl
    ): ArticlesRepository

}