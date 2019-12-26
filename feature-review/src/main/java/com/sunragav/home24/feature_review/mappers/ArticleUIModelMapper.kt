package com.sunragav.home24.feature_review.mappers

import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.feature_review.models.ArticleUIModel
import com.sunragav.home24.utils.Mapper

class ArticleUIModelMapper :Mapper<ArticleDomainEntity, ArticleUIModel>{
    override fun from(model: ArticleUIModel): ArticleDomainEntity {
        return ArticleDomainEntity(
            sku = model.sku,
            imageUrl = model.imageUrl,
            flagged = model.flagged,
            title = model.title,
            reviewed = model.reviewed
        )
    }

    override fun to(entity: ArticleDomainEntity): ArticleUIModel {
        return ArticleUIModel(
            sku = entity.sku,
            imageUrl = entity.imageUrl,
            flagged = entity.flagged,
            title = entity.title,
            reviewed = entity.reviewed
        )
    }


}