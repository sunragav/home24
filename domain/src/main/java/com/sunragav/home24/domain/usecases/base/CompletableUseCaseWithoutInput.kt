package com.sunragav.home24.domain.usecases.base

import io.reactivex.Maybe
import io.reactivex.Scheduler

abstract class CompletableUseCaseWithoutInput constructor(
    private val backgroundScheduler: Scheduler,
    private val foregroundScheduler: Scheduler
) {
    protected abstract fun generateCompletable(): Maybe<Int>

    fun buildUseCase(): Maybe<Int> {
        return generateCompletable()
            .subscribeOn(backgroundScheduler)
            .observeOn(foregroundScheduler)
    }
}