package com.sunragav.home24.localdata.datasource


import com.sunragav.home24.localdata.dao.ArticlesDao
import com.sunragav.home24.localdata.datasource.utils.TestDataContainer.Companion.getArticles
import com.sunragav.home24.localdata.mappers.ArticleLocalDataMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

typealias Callback = ()->Unit
class LocalRepositoryImplTest {
    private lateinit var localDataSourceImpl: LocalRepositoryImpl

    private var articlesDAO: ArticlesDao = mockk()
    private var articleLocalMapper = ArticleLocalDataMapper()
    @Before
    fun setup() {
        localDataSourceImpl = LocalRepositoryImpl(
            articleLocalMapper,
            articlesDAO
        )
    }

    @Test
    fun test_insert_success() {
        val articles = getArticles()
        val articleEntityList = articles.map { articleLocalMapper.from(it) }
        every { articlesDAO.insert(articles) } returns Completable.complete()
        localDataSourceImpl.insert(articleEntityList).test()
        verify(exactly = 1) { articlesDAO.insert(articles) }
    }

    @Test
    fun test_insert_error() {
        val message = "Error"
        val callBack: Callback = mockk()
        every { articlesDAO.insert(any()) } returns Completable.error(Throwable(message))
        localDataSourceImpl.insert(
            getArticles().map { articleLocalMapper.from(it) }
        ).test()
            .assertSubscribed()
            .assertError { it.message == message }
        verify(exactly = 0) { callBack.invoke() }
    }

    @Test
    fun test_getArticleById_success() {
        val articles = getArticles()
        val articleEntityList = articles.map { articleLocalMapper.from(it) }
        every { articlesDAO.getArticleById(any()) } returns Observable.just(articles[0])
        localDataSourceImpl.getArticleById("123").test()
            .assertSubscribed()
            .assertValueCount(1)
            .assertValue { it == articleEntityList[0] }
    }

    @Test
    fun test_getArticleById_failure() {
        every { articlesDAO.getArticleById(any()) } returns Observable.empty()
        localDataSourceImpl.getArticleById("123").test()
            .assertSubscribed()
            .assertNoValues()
    }


    @Test
    fun test_update_success() {
        val articles = getArticles()
        val articleEntityList = articles.map { articleLocalMapper.from(it) }
        val callBack: Callback = mockk()
        val errorCallback: Callback = mockk()
        every { callBack.invoke() } returns Unit
        every { errorCallback.invoke() } returns Unit

        every { articlesDAO.update(any()) } returns Completable.complete()
        val flaggedArticle = articleEntityList[0].copy(flagged = true)
        localDataSourceImpl.update(
            articleEntity = flaggedArticle
        ).andThen { callBack.invoke() }
            .doOnError { errorCallback.invoke() }
            .test()
            .assertSubscribed()
            .assertNoErrors()

        verify(exactly = 1) { articlesDAO.update(articleLocalMapper.to(flaggedArticle)) }
        verify(exactly = 1) { callBack.invoke() }
        verify(exactly = 0) { errorCallback.invoke() }
    }


    @Test
    fun test_update_error() {
        val articles = getArticles()
        val articleEntityList = articles.map { articleLocalMapper.from(it) }
        val callBack: Callback = mockk()
        val errorCallback: Callback = mockk()
        every { callBack.invoke() } returns Unit
        every { errorCallback.invoke() } returns Unit

        every { articlesDAO.update(any()) } returns Completable.error(Throwable(message = "Fake Error"))
        val flaggedArticle = articleEntityList[0].copy(flagged = true)
        localDataSourceImpl.update(
            articleEntity = flaggedArticle
        ).andThen { callBack.invoke() }
            .doOnError { errorCallback.invoke() }
            .test()
            .assertSubscribed()
            .assertError{it.message == "Fake Error"}

        verify(exactly = 1) { articlesDAO.update(articleLocalMapper.to(flaggedArticle)) }
        verify(exactly = 0) { callBack.invoke() }
        verify(exactly = 1) { errorCallback.invoke() }
    }
}