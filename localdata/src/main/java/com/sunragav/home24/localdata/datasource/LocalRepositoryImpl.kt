package com.sunragav.home24.localdata.datasource

import androidx.paging.DataSource
import com.sunragav.home24.data.contract.LocalRepository
import com.sunragav.home24.data.contract.Request
import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.domain.usecases.GetArticlesAction
import com.sunragav.home24.localdata.dao.ArticlesDao
import com.sunragav.home24.localdata.dao.RequestDao
import com.sunragav.home24.localdata.mappers.ArticleLocalDataMapper
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val articleLocalDataMapper: ArticleLocalDataMapper,
    private val articlesDao: ArticlesDao,
    private val requestDao: RequestDao
) : LocalRepository {
    override fun insert(articleEntityList: List<ArticleDomainEntity>): Completable {
        return articlesDao.insert(articleEntityList.map { articleLocalDataMapper.to(it) })
    }

    override fun getArticlesDatasourceFactory(param: GetArticlesAction.Params): DataSource.Factory<Int, ArticleDomainEntity> {
        return (if (param.flagged) articlesDao.getFavorites() else if (param.reviewed) articlesDao.getReviewedArticles() else articlesDao.getArticles())
            .map { articleLocalDataMapper.from(it) }
    }

    override fun getArticleById(uniqueIdentifier: String): Observable<ArticleDomainEntity> {
        return articlesDao.getArticleById(uniqueIdentifier).map { articleLocalDataMapper.from(it) }
    }

    override fun update(articleEntity: ArticleDomainEntity): Completable {
        return articlesDao.update(articleLocalDataMapper.to(articleEntity))
    }

    override fun clearAllLikes(): Maybe<Int> {
        return articlesDao.clearLikesFromArticles()
    }


    override fun getPreviousRequest(): Maybe<Request> {
        return requestDao.getRequest()
            .map { Request(it.id, it.offset, it.limit) }
    }

    override fun updatePreviousRequest(request: Request): Completable {
        return with(request) {
            requestDao.insert(
                com.sunragav.home24.localdata.models.Request(
                    id,
                    offset,
                    limit
                )
            )
        }
    }

    override fun clearPreviousRequest(): Completable {
        return requestDao.clearRequest()
    }

}