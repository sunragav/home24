package com.sunragav.home24.domain.repository

import com.sunragav.home24.domain.models.ArticleEntity
import com.sunragav.home24.domain.usecases.GetArticlesAction
import com.sunragav.home24.domain.usecases.GetArticlesAction.GetArticlesActionResult
import io.reactivex.Completable
import io.reactivex.Observable

interface ArticlesRepository {
    fun getArticle(sku: String): Observable<ArticleEntity>

    fun getArticles(query: GetArticlesAction.Params): GetArticlesActionResult

    fun getFavoriteArticles(): GetArticlesActionResult

    fun updateArticle(articleEntity: ArticleEntity): Completable
}