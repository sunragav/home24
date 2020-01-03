package com.sunragav.home24.feature_selection.views.listeners

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.sunragav.feature_selection.R
import com.sunragav.home24.android_utils.animation.animateBtn
import com.sunragav.home24.feature_selection.viewpager.adapter.PagedArticlesAdapter
import com.sunragav.home24.feature_selection.views.SelectionFragmentDirections
import com.sunragav.home24.presentation.viewmodels.ArticlesViewModel
import java.lang.ref.WeakReference

class ClickListener(
    private val viewPagerRef: WeakReference<ViewPager2>,
    private val viewModel: ArticlesViewModel
) {
    fun onLiked(view: View) {
        animateBtn(view as AppCompatButton) {

            navigateToNext(liked = true)
        }
    }

    fun onUndo(view: View) {
        animateBtn(view as AppCompatButton) {

            val viewPager = viewPagerRef.get()!!
            if (viewPager.currentItem > 0) {
                viewPager.currentItem--
                viewModel.currentItem.value = viewPager.currentItem
            }
        }
    }

    private fun navigateToNext(liked: Boolean) {
        val viewPager = viewPagerRef.get()!!
        if (viewModel.isReadyToReview.get() == false && viewModel.canNavigate.value == true) {
            with(viewPager) {
                val pagedArticlesAdapter = (adapter as PagedArticlesAdapter)
                val list = pagedArticlesAdapter.currentList
                list?.get(currentItem)?.let {
                    viewModel.currentItem.value = currentItem
                    viewModel.handleLikeDislike(it, liked) {
                        pagedArticlesAdapter.notifyItemChanged(currentItem)
                        viewPager.currentItem++
                    }
                }
            }
        }
    }


    fun onDisliked(view: View) {
        animateBtn(view as AppCompatButton) {
            navigateToNext(liked = false)
        }
    }

    fun onReview(view: View) {
        animateBtn(view as AppCompatButton) {
            val navController = view.findNavController()
            val action = SelectionFragmentDirections.actionSelectionFragmentToReviewFragment()
            if (navController.currentDestination?.id == R.id.selectionFragment) {
                navController.navigate(action)
            }
        }
    }
}