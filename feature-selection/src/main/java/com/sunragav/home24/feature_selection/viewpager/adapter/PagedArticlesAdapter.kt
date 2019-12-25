package com.sunragav.home24.feature_selection.viewpager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sunragav.feature_selection.R
import com.sunragav.feature_selection.databinding.FragmentSelectionBinding
import com.sunragav.feature_selection.databinding.ViewpagerItemViewBinding
import com.sunragav.home24.domain.models.ArticleDomainEntity
import com.sunragav.home24.feature_selection.mappers.ArticleUIModelMapper
import com.sunragav.home24.feature_selection.viewpager.viewholder.ArticleViewHolder


class PagedArticlesAdapter(
    private val articleUIModelMapper: ArticleUIModelMapper
) :
    PagedListAdapter<ArticleDomainEntity, ArticleViewHolder>(ARTICLES_COMPARATOR) {
    init {
        setHasStableIds(true)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemArticleBinding = DataBindingUtil.inflate<ViewpagerItemViewBinding>(
            layoutInflater,
            R.layout.viewpager_item_view,
            parent,
            false
        )
        return ArticleViewHolder(itemArticleBinding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, articleUIModelMapper) }
    }

    companion object {
        private val ARTICLES_COMPARATOR = object : DiffUtil.ItemCallback<ArticleDomainEntity>() {
            override fun areItemsTheSame(
                oldItem: ArticleDomainEntity,
                newItem: ArticleDomainEntity
            ): Boolean =
                oldItem.sku == newItem.sku

            override fun areContentsTheSame(
                oldItem: ArticleDomainEntity,
                newItem: ArticleDomainEntity
            ): Boolean =
                oldItem == newItem
        }
    }
}
