package com.sunragav.home24.remotedata.datasource.utils

import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.remotedata.mapper.ArticleRemoteDataMapper
import com.sunragav.home24.remotedata.models.ArticleRemoteData
import com.sunragav.home24.remotedata.models.DataWrapper
import com.sunragav.home24.remotedata.models.Embedded


class TestRemoteDataContainer {
    companion object {
        private val articleRemoteDataMapper = ArticleRemoteDataMapper()
        fun getArticleRemoteDatasList(): List<ArticleRemoteData> {
            return getArticleDomainEntityList().map { articleRemoteDataMapper.to(it) }
        }

        private fun getArticleRemoteDataEntity(): ArticleDomainEntity {
            return ArticleDomainEntity(
                sku="143",
               imageUrl = "Fake image",
                flagged = false,
                title = "Fake title"
            )
        }

        private fun getArticleDomainEntityList() =
            listOf(
                getArticleRemoteDataEntity(),
                getArticleRemoteDataEntity().copy(sku = "123"),
                getArticleRemoteDataEntity().copy(sku = "134")
            )

        fun getDataWrapper(articles: List<ArticleRemoteData>? = null): DataWrapper<List<ArticleRemoteData>> {
            return DataWrapper(
                _embedded = Embedded(articles?.let { it } ?: getArticleRemoteDatasList())
            )
        }
    }
}