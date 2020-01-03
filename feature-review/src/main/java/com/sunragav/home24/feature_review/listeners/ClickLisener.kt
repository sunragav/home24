package com.sunragav.home24.feature_review.listeners

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.sunragav.home24.android_utils.animation.animate
import com.sunragav.home24.presentation.viewmodels.ArticlesViewModel

class ClickListener(
    private val viewModel: ArticlesViewModel,
    private val listViewUIHandler: () -> Unit,
    private val gridViewUIHandler: () -> Unit
) {
    fun onListView(view: View) {
        (view as AppCompatButton).animate {
            viewModel.isListView.set(true)
            listViewUIHandler.invoke()
        }
    }

    fun onGridView(view: View) {
        (view as AppCompatButton).animate {
            viewModel.isListView.set(false)
            gridViewUIHandler.invoke()
        }
    }
}