package com.sunragav.home24.domain.models

data class ArticleEntity(
    val sku: String,
    val imageUrl: String,
    val flagged: Boolean = false
)