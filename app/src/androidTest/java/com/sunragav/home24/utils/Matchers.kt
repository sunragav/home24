package com.sunragav.home24.utils

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.viewpager2.widget.ViewPager2
import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.feature_selection.viewpager.adapter.PagedArticlesAdapter
import org.hamcrest.Description
import org.hamcrest.Matcher


class CustomMatchers {


    companion object {

        fun atPositionWithArticle_ImageUrl(
            position: Int,
            article: ArticleDomainEntity
        ): Matcher<View> {
            return object : BoundedMatcher<View, ViewPager2>(ViewPager2::class.java) {
                override fun describeTo(description: Description) {
                    description.appendText("has item at position $position: ")
                    // itemMatcher.describeTo(description)
                }

                override fun matchesSafely(item: ViewPager2): Boolean {
                    val viewItem =
                        (item.adapter as PagedArticlesAdapter).currentList?.get(position) as ArticleDomainEntity
                    return article.imageUrl == viewItem.imageUrl
                }
            }
        }

    }
}