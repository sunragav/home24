package com.sunragav.home24.domain.usecases

import com.sunragav.home24.domain.repository.ArticlesRepository
import com.sunragav.home24.domain.utils.TestDataContainer
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations


class UpdateArticleActionTest {
    private lateinit var updateArticleAction: UpdateArticleAction

    @Mock
    private lateinit var articleDataRepository: ArticlesRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        updateArticleAction = UpdateArticleAction(
            articleDataRepository,
            Schedulers.trampoline(),
            Schedulers.trampoline()
        )
    }

    @Test
    fun test_updateArticleAction_success() {
        val article = TestDataContainer.getArticle()

        Mockito.`when`(articleDataRepository.updateArticle(article))
            .thenReturn(Completable.complete())

        val testObserver = updateArticleAction.buildUseCase(
            article
        ).test()

        verify(articleDataRepository, times(1))
            .updateArticle(article)

        testObserver
            .assertSubscribed()
            .assertComplete()
    }

    @Test
    fun test_updateArticleAction_error() {
        val article = TestDataContainer.getArticle()
        val errorMsg = "ERROR OCCURRED"

        Mockito.`when`(articleDataRepository.updateArticle(article))
            .thenReturn(Completable.error(Throwable(errorMsg)))

        val testObserver = updateArticleAction
            .buildUseCase(article).test()

        verify(articleDataRepository, times(1))
            .updateArticle(article)

        testObserver
            .assertSubscribed()
            .assertError { it.message?.equals(errorMsg) ?: false }
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_updateArticleActionNoParameters_error() {
        val testObserver = updateArticleAction.buildUseCase().test()
        testObserver.assertSubscribed()
    }
}