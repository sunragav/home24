package com.sunragav.home24.feature_review.listeners

import android.view.View
import com.sunragav.home24.presentation.viewmodels.ArticlesViewModel
import com.sunragav.home24.presentation.viewmodels.ArticlesViewModel.Companion.GRID
import com.sunragav.home24.presentation.viewmodels.ArticlesViewModel.Companion.LIST

class ClickListener(
    private val viewModel: ArticlesViewModel,
    private val listView: () -> Unit,
    private val gridView: () -> Unit
) {
    fun onListView(view: View) {
        viewModel.toggleListGridView.set(LIST)
        listView.invoke()
    }

    fun onGridView(view: View) {
        viewModel.toggleListGridView.set(GRID)
        gridView.invoke()
    }
}