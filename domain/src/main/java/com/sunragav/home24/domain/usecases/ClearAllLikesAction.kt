package com.sunragav.home24.domain.usecases

import com.sunragav.home24.domain.qualifiers.Background
import com.sunragav.home24.domain.qualifiers.Foreground
import com.sunragav.home24.domain.repository.ArticlesRepository
import com.sunragav.home24.domain.usecases.base.CompletableUseCaseWithoutInput
import io.reactivex.Maybe
import io.reactivex.Scheduler
import javax.inject.Inject

class ClearAllLikesAction @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : CompletableUseCaseWithoutInput(
    backgroundScheduler,
    foregroundScheduler
) {
    override fun generateCompletable(): Maybe<Int> {
        return articlesRepository.clearAllLikes()
    }
}