package com.sunragav.home24.domain.usecases

import androidx.paging.DataSource
import androidx.paging.PagedList
import com.sunragav.home24.domain.models.ArticleEntity
import com.sunragav.home24.domain.repository.ArticlesRepository
import javax.inject.Inject

class GetArticlesAction @Inject constructor(
    private val articlesRepository: ArticlesRepository
) {
    companion object {
        const val PAGE_SIZE = 20
    }


    data class Params(
        val limit: Int = PAGE_SIZE,
        val offset: Int = 0,
        val flagged:Boolean = false
    )

    fun buildUseCase(input: Params): GetArticlesActionResult {
        return articlesRepository.getArticles(input)
    }

    class GetArticlesActionResult(
        val dataSource: DataSource.Factory<Int, ArticleEntity>,
        val boundaryCallback: PagedList.BoundaryCallback<ArticleEntity>
    )


}