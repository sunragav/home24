package com.sunragav.home24.domain.utils

import com.sunragav.home24.domain.models.ArticleDomainEntity


class TestDataContainer {

    companion object {
        fun getArticle(): ArticleDomainEntity {
            return ArticleDomainEntity(
                sku = "143",
                imageUrl = "thumbnail.jpg",
                flagged = false,
                title = "Fake title",
                reviewed = false
            )
        }
    }
}
