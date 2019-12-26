package com.sunragav.home24.domain.models

data class ArticleDomainEntity(
    val sku: String,
    val imageUrl: String,
    val flagged: Boolean = false,
    val title: String,
    val reviewed: Boolean
)