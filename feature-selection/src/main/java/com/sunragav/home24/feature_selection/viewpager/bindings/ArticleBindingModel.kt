package com.sunragav.home24.feature_selection.viewpager.bindings

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import com.sunragav.home24.feature_selection.models.ArticleUIModel

class ArticleBindingModel(
    var articleUIModel: ArticleUIModel
) : BaseObservable() {
    val imageUrl: ObservableField<String> = ObservableField(articleUIModel.imageUrl)

}