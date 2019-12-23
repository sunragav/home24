package com.sunragav.home24.data.contract

import com.sunragav.home24.domain.models.ArticleDomainEntity
import io.reactivex.Observable
import io.reactivex.Single

interface RemoteRepository {
    fun getArticles(
         lastRequestedPage: Int, limit: Int
    ): Single<List<ArticleDomainEntity>>

}