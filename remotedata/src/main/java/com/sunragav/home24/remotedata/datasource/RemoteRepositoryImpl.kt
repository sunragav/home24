package com.sunragav.home24.remotedata.datasource

import com.sunragav.home24.data.contract.RemoteRepository
import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.domain.qualifiers.Background
import com.sunragav.home24.remotedata.api.ArticleService
import com.sunragav.home24.remotedata.mapper.ArticleRemoteDataMapper
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val articleService: ArticleService,
    private val articleRemoteDataMapper: ArticleRemoteDataMapper,
    @Background private val backgroundThread: Scheduler

) : RemoteRepository {
    override fun getArticles(
        lastRequestedPage: Int,
        limit: Int
    ): Single<List<ArticleDomainEntity>> {
        return articleService.getArticles(lastRequestedPage * limit, limit)
            .map { dataWrapper ->
                dataWrapper._embedded.articles
                    .map { articleRemoteDataMapper.from(it) }
            }.subscribeOn(backgroundThread)
    }
}