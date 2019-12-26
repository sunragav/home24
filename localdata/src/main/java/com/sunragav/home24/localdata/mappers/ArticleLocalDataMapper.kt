package com.sunragav.home24.localdata.mappers

import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.localdata.models.ArticleLocalData
import com.sunragav.home24.localdata.qualifiers.LocalDataMapper
import com.sunragav.home24.utils.Mapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@LocalDataMapper
class ArticleLocalDataMapper @Inject constructor() : Mapper<ArticleDomainEntity, ArticleLocalData> {
    override fun from(model: ArticleLocalData): ArticleDomainEntity {
        return ArticleDomainEntity(
            sku = model.sku,
            imageUrl = model.imageUrl,
            flagged = model.flagged,
            title = model.title
        )
    }

    override fun to(entity: ArticleDomainEntity): ArticleLocalData {
        return ArticleLocalData(
            sku = entity.sku,
            imageUrl = entity.imageUrl,
            flagged = entity.flagged,
            title = entity.title
        )
    }

}