package com.sunragav.home24.domain.utils

import com.sunragav.home24.domain.models.ArticleEntity


class TestDataContainer {

    companion object {
        fun getArticle(): ArticleEntity {
            return ArticleEntity(
                sku = "143",
                imageUrl = "thumbnail.jpg",
                flagged = false
            )
        }
    }
}
