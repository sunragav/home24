package com.sunragav.home24.di

import com.sunragav.home24.domain.qualifiers.Background
import com.sunragav.home24.domain.qualifiers.Foreground
import com.sunragav.home24.domain.qualifiers.ReviewCount
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Module
class DomainModule {
    @Singleton
    @Provides
    @Background
    fun providesBackgroundScheduler(): Scheduler {
        return Schedulers.trampoline()
    }

    @Singleton
    @Provides
    @Foreground
    fun providesForegroundScheduler(): Scheduler {
        return Schedulers.trampoline()
    }

    @Provides
    fun provideCompoisteDisposable() = CompositeDisposable()

    @Singleton
    @Provides
    @ReviewCount
    fun providesReviewCount() = "2"

}