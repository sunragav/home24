package com.sunragav.home24.localdata.datasource


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.paging.LimitOffsetDataSource
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.sunragav.home24.localdata.dao.ArticlesDao
import com.sunragav.home24.localdata.db.ArticlesDB
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import utils.TestDataContainer

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class ArticlesDAOTest {
    companion object {
        private const val LIMIT = 20
    }

    private lateinit var articlesDB: ArticlesDB
    private lateinit var articlesListDAO: ArticlesDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        articlesDB = Room.inMemoryDatabaseBuilder(context, ArticlesDB::class.java)
            .allowMainThreadQueries()
            .build()

        articlesListDAO = articlesDB.getArticlesDao()
    }

    @After
    fun tearDown() {
        articlesListDAO.clearLikesFromArticles()
        articlesDB.close()
    }

    @Test
    fun test_saveAndRetrieveArticlesList() {

        val articlesList = TestDataContainer.getArticles()
        val articlesCount = articlesList.size

        articlesListDAO.insert(articlesList).test()

        val result = (articlesListDAO.getArticles().create() as LimitOffsetDataSource).loadRange(
            0,
            LIMIT
        )
        assertThat(result.size, equalTo(articlesCount))

        articlesListDAO.getArticleById("143").test()
            .assertSubscribed()
            .assertValue { it == articlesList[0] }
            .assertNoErrors()
            .assertNotComplete() // As Room Observables are kept alive
    }

    @Test
    fun test_updateArticles() {

        val article = TestDataContainer.getArticle()

        articlesListDAO.insert(listOf(article)).test()
        assertThat(article.flagged, equalTo(true))
        assertThat(article.sku, equalTo("143"))

        articlesListDAO.update(article.copy( flagged = false)).test()

        articlesListDAO.getArticleById("143")
            .test()
            .assertSubscribed()
            .assertValue {
                it.flagged.not()
            }
            .assertNotComplete() // As Room Observables are kept alive

    }

    @Test
    fun test_clearLikesFromArticles() {
        val articlesList = TestDataContainer.getArticles()

        val likedArticles = articlesList.map { it.copy(flagged = true, reviewed = true) }
        articlesListDAO.insert(likedArticles).subscribe()

        var result =
            (articlesListDAO.getArticles().create() as LimitOffsetDataSource).loadRange(
                0,
                LIMIT
            )
        assertThat(result.size, equalTo(articlesList.size))


        articlesListDAO.clearLikesFromArticles().test()

        result = (articlesListDAO.getArticles().create() as LimitOffsetDataSource).loadRange(
            0,
            LIMIT
        )
        assertThat(result.all { !it.flagged && !it.reviewed }, equalTo(true))

    }

}