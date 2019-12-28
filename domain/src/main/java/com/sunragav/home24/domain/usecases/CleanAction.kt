package com.sunragav.home24.domain.usecases

import com.sunragav.home24.domain.repository.ArticlesRepository
import javax.inject.Inject

class CleanAction @Inject constructor(
    private val articlesRepository: ArticlesRepository
) {
    fun execute() {
        articlesRepository.clean()
    }
}