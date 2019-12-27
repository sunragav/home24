package com.sunragav.home24.utils

import com.sunragav.home24.remotedata.api.ArticleService
import com.sunragav.home24.remotedata.models.ArticleRemoteData
import com.sunragav.home24.remotedata.models.DataWrapper
import io.reactivex.Single


class FakeArticleService : ArticleService {

    override fun getArticles(
        offset: Int?,
        limit: Int?
    ): Single<DataWrapper<List<ArticleRemoteData>>> {
        return Single.just(dataWrapper)
    }
}