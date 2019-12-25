package com.sunragav.home24.data.contract

import androidx.paging.DataSource
import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.domain.usecases.GetArticlesAction
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable

interface LocalRepository {
    fun insert(
        articleEntityList: List<ArticleDomainEntity>
    ): Completable

    fun getArticlesDatasourceFactory(param: GetArticlesAction.Params): DataSource.Factory<Int, ArticleDomainEntity>
    fun getArticleById(uniqueIdentifier: String): Observable<ArticleDomainEntity>
    fun update(articleEntity: ArticleDomainEntity): Completable
    fun clearAllLikes(): Maybe<Int>

    fun getPreviousRequest(): Maybe<Request>
    fun updatePreviousRequest(request: Request): Completable
    fun clearPreviousRequest():Completable
}