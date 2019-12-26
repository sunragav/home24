package com.sunragav.home24.domain.usecases

import com.sunragav.home24.domain.repository.ArticlesRepository
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations


@RunWith(JUnit4::class)
class GetArticlesActionTest {
    companion object {
        private val query = GetArticlesAction.Params(
            flagged = false,
            reviewed = true
        )
    }

    private lateinit var getArticlesAction: GetArticlesAction

    @Mock
    lateinit var articleDataRepository: ArticlesRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        getArticlesAction = GetArticlesAction(
            articleDataRepository
        )
    }

    @Test
    fun test_getArticlesAction_Success() {

        val result =
            GetArticlesAction.GetArticlesActionResult(mockk(), mockk())

        Mockito.`when`(
            articleDataRepository.getArticles(
                query
            )
        ).thenReturn(result)


        getArticlesAction.buildUseCase(query)


        verify(articleDataRepository, times(1)).getArticles(query)


    }

}

