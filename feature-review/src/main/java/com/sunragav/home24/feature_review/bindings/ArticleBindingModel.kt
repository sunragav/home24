package com.sunragav.home24.feature_review.bindings

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import com.sunragav.home24.feature_review.models.ArticleUIModel

class ArticleBindingModel(
    var articleUIModel: ArticleUIModel
) : BaseObservable() {
    val imageUrl: ObservableField<String> = ObservableField(articleUIModel.imageUrl)
    val title:  ObservableField<String> = ObservableField(articleUIModel.title)
    val liked: ObservableField<Boolean> = ObservableField(articleUIModel.flagged)

}