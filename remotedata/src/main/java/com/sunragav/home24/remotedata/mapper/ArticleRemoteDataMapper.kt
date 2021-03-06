package com.sunragav.home24.remotedata.mapper

import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.remotedata.qualifiers.RemoteDataMapper
import com.sunragav.home24.remotedata.models.ArticleRemoteData
import com.sunragav.home24.remotedata.models.Media
import com.sunragav.home24.utils.Mapper
import javax.inject.Inject
import javax.inject.Singleton

@RemoteDataMapper
@Singleton
class ArticleRemoteDataMapper @Inject constructor() :
    Mapper<ArticleDomainEntity, ArticleRemoteData> {
    override fun from(model: ArticleRemoteData): ArticleDomainEntity {
        return ArticleDomainEntity(
            sku = model.sku,
            imageUrl = model.media?.get(0)?.uri ?: "",
            title =model.title,
            flagged = false,
            reviewed = false
        )
    }

    override fun to(entity: ArticleDomainEntity): ArticleRemoteData {
        return ArticleRemoteData(
            sku = entity.sku,
            media = listOf(Media(entity.imageUrl)),
            title = entity.title
        )
    }
}