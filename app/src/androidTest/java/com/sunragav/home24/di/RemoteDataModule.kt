package com.sunragav.home24.di

import com.squareup.moshi.Moshi
import com.sunragav.home24.BuildConfig
import com.sunragav.home24.data.contract.RemoteRepository
import com.sunragav.home24.remotedata.api.ArticleService
import com.sunragav.home24.remotedata.datasource.RemoteRepositoryImpl
import com.sunragav.home24.remotedata.http.ApiInterceptor
import com.sunragav.home24.remotedata.qualifiers.AppDomain
import com.sunragav.home24.remotedata.qualifiers.Locale
import com.sunragav.home24.utils.FakeArticleService
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [RemoteDataModule.Binders::class])
class RemoteDataModule {

    @Module
    interface Binders {

        @Binds
        fun bindsRemoteRepository(
            remoteDataSourceImpl: RemoteRepositoryImpl
        ): RemoteRepository
    }

    @Provides
    @Locale
    fun provideLocale() = BuildConfig.LOCALE

    @Provides
    @AppDomain
    fun provideAppDomain() = BuildConfig.APP_DOMAIN


    @Provides
    @Singleton
    fun provideArticlesService(retrofit: Retrofit): ArticleService =
        FakeArticleService()


    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .build()

    @Provides
    @Singleton
    fun provideHttpClient(apiInterceptor: ApiInterceptor): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        val level = getInterceptorLevel()
        httpLoggingInterceptor.level = level
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(apiInterceptor)
            .cache(null)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS).build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    private fun getInterceptorLevel(): HttpLoggingInterceptor.Level? {
        return if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BODY
        else
            HttpLoggingInterceptor.Level.NONE
    }

}