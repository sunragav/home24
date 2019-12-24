package com.sunragav.home24.di

import android.app.Application
import android.content.Context
import com.sunragav.home24.feature_review.views.ReviewFragment
import com.sunragav.home24.feature_selection.views.SelectionFragment
import com.sunragav.home24.views.FeatureActivity
import com.sunragav.home24.views.SplashActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AppModule {

    @Binds
    abstract fun bindContext(application: Application): Context

    @ContributesAndroidInjector
    internal abstract fun contributesSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    internal abstract fun contributesFeautreActivity(): FeatureActivity

    @ContributesAndroidInjector
    internal abstract fun contributesSelectionFeautreFragment(): SelectionFragment

    @ContributesAndroidInjector
    internal abstract fun contributesReviewFeautreFragment(): ReviewFragment

}