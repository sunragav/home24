package utils

import com.sunragav.home24.localdata.models.ArticleLocalData


class TestDataContainer {

    companion object {
        fun getArticle(): ArticleLocalData {
            return ArticleLocalData(
                "143",
                "Fake image",
                true
            )
        }

        fun getArticles() =
            listOf(getArticle(), getArticle().copy(sku = "123"), getArticle().copy(sku = "124"))
    }
}
