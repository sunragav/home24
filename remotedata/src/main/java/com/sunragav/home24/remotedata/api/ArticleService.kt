package com.sunragav.home24.remotedata.api

import com.sunragav.home24.remotedata.models.ArticleRemoteData
import com.sunragav.home24.remotedata.models.DataWrapper
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface ArticleService{

    @GET("articles")
    fun getArticles(
        @Query(OFFSET) offset: Int?,
        @Query(LIMIT) limit: Int?
        ): Single<DataWrapper<List<ArticleRemoteData>>>

    companion object {
        const val OFFSET = "offset"
        const val LIMIT = "limit"
    }
}