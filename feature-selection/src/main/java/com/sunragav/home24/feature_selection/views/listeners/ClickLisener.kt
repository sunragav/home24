package com.sunragav.home24.feature_selection.views.listeners

import android.view.View
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.sunragav.feature_selection.R
import com.sunragav.home24.feature_selection.viewpager.adapter.PagedArticlesAdapter
import com.sunragav.home24.presentation.viewmodels.ArticlesViewModel

class ClickListener(
    private val viewPager: ViewPager2,
    private val viewModel: ArticlesViewModel
) {
    fun onLiked(view: View) {

        navigateToNext(liked = true)
    }

    fun onUndo(view: View) {
        if (viewPager.currentItem > 0) {
            viewPager.currentItem--
            viewModel.currentItem.value = viewPager.currentItem
        }
    }

    private fun navigateToNext(liked: Boolean) {
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
        navigateToNext(liked = false)
    }

    fun onReview(view: View) {
        view.findNavController().navigate(R.id.action_selectionFragment_to_reviewFragment)
    }
}