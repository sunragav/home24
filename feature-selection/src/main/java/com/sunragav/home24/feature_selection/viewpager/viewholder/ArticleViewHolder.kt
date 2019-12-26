package com.sunragav.home24.feature_selection.viewpager.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sunragav.feature_selection.databinding.FragmentSelectionBinding
import com.sunragav.feature_selection.databinding.ViewpagerItemViewBinding
import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.feature_selection.mappers.ArticleUIModelMapper
import com.sunragav.home24.feature_selection.viewpager.bindings.ArticleBindingModel
import com.sunragav.home24.feature_selection.views.SelectionFragment


class ArticleViewHolder(private val binding: ViewpagerItemViewBinding) :
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