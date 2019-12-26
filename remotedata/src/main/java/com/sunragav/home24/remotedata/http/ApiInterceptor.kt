package com.sunragav.home24.remotedata.http

import com.sunragav.home24.remotedata.qualifiers.AppDomain
import com.sunragav.home24.remotedata.qualifiers.Locale
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class ApiInterceptor @Inject constructor(
    @AppDomain private val appDomain: String,
    @Locale private val locale: String
) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url()
        val url = originalUrl.newBuilder()
            .addQueryParameter("appDomain", appDomain)
            .addQueryParameter("locale", locale)
            .build()

        val requestBuilder = originalRequest.newBuilder().url(url)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}