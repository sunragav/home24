package utils

import com.sunragav.home24.domain.models.ArticleDomainEntity


class TestDataContainer {

    companion object {
        fun getArticle(): ArticleDomainEntity {
            return ArticleDomainEntity(
                sku="143",
                imageUrl = "dummy.jpg",
                flagged = false
            )
        }
    }
}
