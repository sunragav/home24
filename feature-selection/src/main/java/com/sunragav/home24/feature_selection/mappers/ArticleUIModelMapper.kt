package com.sunragav.home24.feature_selection.mappers

import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.feature_selection.models.ArticleUIModel
import com.sunragav.home24.utils.Mapper
import javax.inject.Inject

class ArticleUIModelMapper :Mapper<ArticleDomainEntity,ArticleUIModel>{
    override fun from(model: ArticleUIModel): ArticleDomainEntity {
        return ArticleDomainEntity(
            sku = model.sku,
            imageUrl = model.imageUrl,
            flagged = model.flagged,
            title = model.title
        )
    }

    override fun to(entity: ArticleDomainEntity): ArticleUIModel {
        return ArticleUIModel(
            sku = entity.sku,
            imageUrl = entity.imageUrl,
            flagged = entity.flagged,
            title = entity.title
        )
    }


}