package com.sunragav.home24.feature_review.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sunragav.feature_review.databinding.ListItemViewBinding
import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.feature_review.mappers.ArticleUIModelMapper
import com.sunragav.home24.feature_review.bindings.ArticleBindingModel


class ArticleViewHolder(private val binding: ListItemViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        articleDomainEntity: ArticleDomainEntity,
        articleUIModelMapper: ArticleUIModelMapper
    ) {
        if (binding.binder == null)
            binding.binder =
                ArticleBindingModel(articleUIModelMapper.to(articleDomainEntity))
        else
            binding.binder!!.articleUIModel = articleUIModelMapper.to(articleDomainEntity)
    }
}