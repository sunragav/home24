package com.sunragav.home24.di

import android.app.Application
import com.sunragav.home24.Home24Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        DomainModule::class,
        DataModule::class,
        LocalDataModule::class,
        RemoteDataModule::class,
        PresentationModule::class,
        AppModule::class
    ]
)
interface AppComponent : AndroidInjector<Home24Application> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: Application): Builder

        fun build(): AppComponent
    }

    override fun inject(app: Home24Application)
}