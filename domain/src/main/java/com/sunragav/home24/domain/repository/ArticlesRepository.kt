package com.sunragav.home24.domain.repository

import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.domain.usecases.GetArticlesAction
import com.sunragav.home24.domain.usecases.GetArticlesAction.GetArticlesActionResult
import io.reactivex.Completable
import io.reactivex.Observable

interface ArticlesRepository {
    fun getArticle(sku: String): Observable<ArticleDomainEntity>

    fun getArticles(query: GetArticlesAction.Params): GetArticlesActionResult

    fun updateArticle(articleDomainEntity: ArticleDomainEntity): Completable
}