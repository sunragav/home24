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
        articlesListDAO.clearArticlesFromTable()
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

        articlesListDAO.update(article.copy(sku = "123", flagged = false))



        articlesListDAO.getArticleById(article.sku)
            .test()
            .assertSubscribed()
            .assertValue {
                !it.flagged && it.sku == "123"
            }
            .assertNotComplete() // As Room Observables are kept alive

    }

    @Test
    fun test_clearArticlesTable() {
        val articlesList = TestDataContainer.getArticles()

        articlesListDAO.insert(articlesList).subscribe()

        var result =
            (articlesListDAO.getArticles().create() as LimitOffsetDataSource).loadRange(
                0,
                LIMIT
            )
        assertThat(result.size, equalTo(articlesList.size))


        articlesListDAO.clearArticlesFromTable()

        result = (articlesListDAO.getArticles().create() as LimitOffsetDataSource).loadRange(
            0,
            LIMIT
        )
        assertThat(result.size, equalTo(0))

    }

}