package com.sunragav.home24.feature_selection.viewpager.viewholder

import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView
import com.sunragav.feature_selection.databinding.ViewpagerItemViewBinding
import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.feature_selection.mappers.ArticleUIModelMapper
import com.sunragav.home24.feature_selection.viewpager.bindings.ArticleBindingModel


class ArticleViewHolder(
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val binding: ViewpagerItemViewBinding
) :
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