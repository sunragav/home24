package com.sunragav.home24.feature_review.listeners

import android.view.View
import com.sunragav.home24.presentation.viewmodels.ArticlesViewModel

class ClickListener(
    private val viewModel: ArticlesViewModel,
    private val listViewUIHandler: () -> Unit,
    private val gridViewUIHandler: () -> Unit
) {
    fun onListView(view: View) {
        viewModel.isListView.set(true)
        listViewUIHandler.invoke()
    }

    fun onGridView(view: View) {
        viewModel.isListView.set(false)
        gridViewUIHandler.invoke()
    }
}