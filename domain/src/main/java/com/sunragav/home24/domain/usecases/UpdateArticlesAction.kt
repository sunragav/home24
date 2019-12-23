package com.sunragav.home24.domain.usecases

import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.domain.qualifiers.Background
import com.sunragav.home24.domain.qualifiers.Foreground
import com.sunragav.home24.domain.repository.ArticlesRepository
import com.sunragav.home24.domain.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import javax.inject.Inject

class UpdateArticleAction @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : CompletableUseCase<ArticleDomainEntity>(
    backgroundScheduler,
    foregroundScheduler
) {

    override fun generateCompletable(input: ArticleDomainEntity?): Completable {
        if (input == null) {
            throw IllegalArgumentException("UpdateArticleAction parameter can't be null")
        }
        return articlesRepository.updateArticle(input)
    }
}