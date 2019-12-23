package com.sunragav.home24.domain.usecases

import com.sunragav.home24.domain.repository.ArticlesRepository
import com.sunragav.home24.domain.utils.TestDataContainer
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetArticleActionTest {

    companion object {
        const val searchKey = "123"
    }

    private lateinit var getArticleAction: GetArticleAction
    private val articlesRepository: ArticlesRepository = mockk()

    @Before
    fun setup() {
        getArticleAction = GetArticleAction(
            articlesRepository,
            Schedulers.trampoline(),
            Schedulers.trampoline()
        )
    }

    @Test
    fun test_getArticleAction_success() {

        val article = TestDataContainer.getArticle()

        every { articlesRepository.getArticle(searchKey) }
            .returns(Observable.just(article))

        val testObserver = getArticleAction.buildUseCase(searchKey).test()

        testObserver
            .assertSubscribed()
            .assertValue { it == article }
    }

    @Test
    fun test_getArticleAction_error() {
        val errorMsg = "ERROR OCCURRED"

        every { articlesRepository.getArticle(searchKey) }
            .returns(Observable.error(Throwable(errorMsg)))

        val testObserver = getArticleAction.buildUseCase(
            searchKey
        ).test()

        testObserver
            .assertSubscribed()
            .assertError { it.message?.equals(errorMsg, false) ?: false }
            .assertNotComplete()
    }
}