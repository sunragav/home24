package com.sunragav.home24.localdata.datasource

import androidx.paging.DataSource
import com.sunragav.home24.data.contract.LocalRepository
import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.domain.qualifiers.Background
import com.sunragav.home24.domain.usecases.GetArticlesAction
import com.sunragav.home24.localdata.dao.ArticlesDao
import com.sunragav.home24.localdata.mappers.ArticleLocalDataMapper
import com.sunragav.home24.localdata.qualifiers.LocalDataMapper
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    @LocalDataMapper private val articleLocalDataMapper: ArticleLocalDataMapper,
    private val articlesDao: ArticlesDao,
    @Background private val backgroundThread: Scheduler
) : LocalRepository {
    override fun insert(articleEntityList: List<ArticleDomainEntity>): Completable {
        return articlesDao.insert(articleEntityList.map { articleLocalDataMapper.to(it) })
    }

    override fun getArticlesDatasourceFactory(param: GetArticlesAction.Params): DataSource.Factory<Int, ArticleDomainEntity> {
        return (if (param.flagged) articlesDao.getFavorites() else articlesDao.getArticles(
        )).map { articleLocalDataMapper.from(it) }
    }

    override fun getArticleById(uniqueIdentifier: String): Observable<ArticleDomainEntity> {
       return articlesDao.getArticleById(uniqueIdentifier).map { articleLocalDataMapper.from(it) }
    }

    override fun update(articleEntity: ArticleDomainEntity): Completable {
        return articlesDao.update(articleLocalDataMapper.to(articleEntity))
    }

    override fun deleteAllArticles(): Completable {
        return articlesDao.clearArticlesFromTable()
    }

}