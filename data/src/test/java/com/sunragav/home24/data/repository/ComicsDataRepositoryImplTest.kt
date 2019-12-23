package com.sunragav.home24.data.repository

import com.sunragav.home24.data.ArticlesRepositoryImpl
import com.sunragav.home24.data.contract.LocalRepository
import com.sunragav.home24.data.contract.RemoteRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import utils.TestDataContainer.Companion.getArticle

class ArticlesRepositoryImplTest {
    companion object {
        const val SEARCH_KEY = "123"
        const val ERROR = "Error msg"
    }

    private lateinit var articlesRepositoryImpl: ArticlesRepositoryImpl

    private val localRepository: LocalRepository = mockk()
    private val remoteRepository: RemoteRepository = mockk()


    @Before
    fun setup() {
        articlesRepositoryImpl =
            ArticlesRepositoryImpl(mockk(), localRepository, remoteRepository, mockk(), mockk(),
                mockk())
    }

    @Test
    fun test_getArticle_success() {
        val articles = getArticle()
        every {
            localRepository.getArticleById(any())
        }.returns(Observable.just(articles))
        every {
            remoteRepository.getArticleById(any())
        }.returns(Observable.just(articles))

        val articlesObservable = articlesRepositoryImpl.getArticle(SEARCH_KEY)
        val testObserver = articlesObservable.test()
        testObserver
            .assertSubscribed()
            .assertValue { it == articles }
            .assertValueCount(1)
    }


    @Test
    fun test_getArticle_error() {
        val articles = getArticle()

        //Should fail when local db errors
        every {
            localRepository.getArticleById(any())
        } returns (Observable.error(Throwable(ERROR)))
        every {
            remoteRepository.getArticleById(any())
        }.returns(Observable.just(articles))
        val articlesObservable = articlesRepositoryImpl.getArticle(SEARCH_KEY)

        var testObserver = articlesObservable.test()
        testObserver
            .assertSubscribed()
            .assertError { it.message?.equals(ERROR, false) ?: false }
            .assertNotComplete()


        //Should not fail when remote service errors but local db has value, because
        //when local db has data there is no need for the remote service call
        every {
            localRepository.getArticleById(any())
        } returns (Observable.just(articles))
        every {
            remoteRepository.getArticleById(any())
        } returns (Observable.error(Throwable(ERROR)))

        testObserver = articlesRepositoryImpl.getArticle(SEARCH_KEY).test()
        testObserver
            .assertSubscribed()
            .assertNoErrors()
            .assertComplete()
    }

    @Test
    fun test_updateArticle_success() {

        every {
            localRepository.update(any())
        }.returns(Completable.complete())

        val articlesObservable = articlesRepositoryImpl.updateArticle(mockk())
        val testObserver = articlesObservable.test()
        testObserver
            .assertSubscribed()
            .assertComplete()
    }


    @Test
    fun test_updateArticle_error() {
        every {
            localRepository.update(any())
        } returns (Completable.error(Throwable(ERROR)))
        val articlesObservable = articlesRepositoryImpl.updateArticle(mockk())

        val testObserver = articlesObservable.test()
        testObserver
            .assertSubscribed()
            .assertError { it.message?.equals(ERROR, false) ?: false }
            .assertNotComplete()
    }
}