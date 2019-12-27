package com.sunragav.home24

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.sunragav.home24.feature_selection.views.FeatureActivity
import com.sunragav.home24.remotedata.mapper.ArticleRemoteDataMapper
import com.sunragav.home24.utils.CustomMatchers.Companion.atPositionWithArticle_ImageUrl
import com.sunragav.home24.utils.RepositoryStateIdlingResource
import com.sunragav.home24.utils.article1
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4ClassRunner::class)
class ComicsListFeatureActivityTest @Inject constructor() {

    @get:Rule
    val rule = ActivityTestRule<FeatureActivity>(FeatureActivity::class.java)

    private lateinit var repositoryIdlingResource: RepositoryStateIdlingResource
    private val articleRemoteDataMapper = ArticleRemoteDataMapper()


    @Before
    fun setup() {
        repositoryIdlingResource = RepositoryStateIdlingResource(rule.activity.repositoryStateRelay)
    }

    @Test
    fun test_article_displayed() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_start)).perform(click())
        IdlingRegistry.getInstance().register(repositoryIdlingResource)
        Espresso.onView(ViewMatchers.withId(R.id.vp_articles)).check(
            matches(
                atPositionWithArticle_ImageUrl(
                    0,
                    articleRemoteDataMapper.from(article1)
                )
            )
        )
    }

    @Test
    fun test_article_like_test() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_start)).perform(click())
        IdlingRegistry.getInstance().register(repositoryIdlingResource)
        Espresso.onView(ViewMatchers.withId(R.id.tv_like_count)).check(matches(withText("0/2")))
        Espresso.onView(ViewMatchers.withId(R.id.btn_undo)).check(matches(not(isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.btn_like)).perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.tv_like_count)).check(matches(withText("1/2")))
        Espresso.onView(ViewMatchers.withId(R.id.btn_undo)).check(matches(isDisplayed()))
    }

    @Test
    fun test_article_review_count_text_displayed() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_start)).perform(click())
        IdlingRegistry.getInstance().register(repositoryIdlingResource)
        Espresso.onView(ViewMatchers.withId(R.id.tv_like_count)).check(matches(withText("0/2")))
    }


    @Test
    fun test_article_review_screen_launch() {
        Espresso.onView(ViewMatchers.withId(R.id.btn_start)).perform(click())
        IdlingRegistry.getInstance().register(repositoryIdlingResource)
        Espresso.onView(ViewMatchers.withId(R.id.tv_like_count)).check(matches(withText("0/2")))
        Espresso.onView(ViewMatchers.withId(R.id.btn_like)).perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.btn_like)).perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.btn_review)).check(matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.tv_like_count)).check(matches(not(isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.btn_review)).perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.rv_articles_list)).check(matches(isDisplayed()))
    }


    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(repositoryIdlingResource)
    }
}

