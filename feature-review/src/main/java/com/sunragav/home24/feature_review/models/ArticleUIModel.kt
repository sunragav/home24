package com.sunragav.home24.feature_review.models

data class ArticleUIModel (
    val sku: String,
    val imageUrl: String,
    val flagged: Boolean,
    val title: String,
    val reviewed: Boolean
)
