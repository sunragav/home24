package com.sunragav.home24.remotedata.datasource


import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.remotedata.api.ArticleService
import com.sunragav.home24.remotedata.datasource.utils.TestRemoteDataContainer.Companion.getArticleRemoteDatasList
import com.sunragav.home24.remotedata.datasource.utils.TestRemoteDataContainer.Companion.getDataWrapper
import com.sunragav.home24.remotedata.mapper.ArticleRemoteDataMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class RemoteRepositoryImplTest {

    private var articlesService: ArticleService = mockk()
    private val articlesRemoteMapper = ArticleRemoteDataMapper()
    private lateinit var networkDataSource: RemoteRepositoryImpl
    @Before
    fun setUp() {
        networkDataSource = RemoteRepositoryImpl(
            articleService= articlesService,
            articleRemoteDataMapper = articlesRemoteMapper,
            backgroundThread = Schedulers.trampoline())
    }

    @Test
    fun test_getArticles_success() {
        val dataWrapper = getDataWrapper()
        val articles = getArticleRemoteDatasList()
        var result: List<ArticleDomainEntity>? = null
        var error: Throwable? = null

        every {
            articlesService.getArticles(
                any(),
                any()
            )
        } returns (Single.just(dataWrapper))


        networkDataSource.getArticles(0, 20).subscribe({result=it},{error=it})

        verify (exactly = 1){ articlesService.getArticles(any(), any()) }
        assertThat(
            result?.zip(articles)?.all { it.first == articlesRemoteMapper.from(it.second) },
            equalTo(true)
        )
        assertNull(error)
        assertNotNull(result)
    }

    @Test
    fun test_getArticles_failure() {
        val exception = Throwable("Network Error!!")
        var result: List<ArticleDomainEntity>? = null
        var error: Throwable? = null
        every {
            articlesService.getArticles(
                any(),
                any()
            )
        } returns (Single.error(exception))

        networkDataSource.getArticles(0, 20).subscribe({result=it},{error =it})

        verify { articlesService.getArticles(any(), any()) }
        assertNotNull(error)
        assertNull(result)
    }

}