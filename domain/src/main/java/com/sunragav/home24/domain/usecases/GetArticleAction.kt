package com.sunragav.home24.domain.usecases

import com.sunragav.home24.domain.models.ArticleEntity
import com.sunragav.home24.domain.qualifiers.Background
import com.sunragav.home24.domain.qualifiers.Foreground
import com.sunragav.home24.domain.repository.ArticlesRepository
import com.sunragav.home24.domain.usecases.base.ObservableUseCase
import io.reactivex.Observable
import io.reactivex.Scheduler
import javax.inject.Inject


class GetArticleAction @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : ObservableUseCase<ArticleEntity, String>(backgroundScheduler, foregroundScheduler) {

    override fun generateObservable(input: String): Observable<ArticleEntity> {
        return articlesRepository.getArticle(input)
    }
}