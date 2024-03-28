package com.myproject.cloudbridge.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myproject.cloudbridge.databinding.StoreItemRecentSearchedBinding
import com.myproject.cloudbridge.datasource.local.entity.RecentlySearchKeywordEntity

class RecentSearchAdapter(
    private val rootClickListener: (String) -> Unit,
    private val delButtonClickListener: (Long) -> Unit
): ListAdapter<RecentlySearchKeywordEntity, RecentSearchAdapter.RecentSearchViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return RecentSearchViewHolder(
            StoreItemRecentSearchedBinding.inflate(inflater, parent, false),
            rootClickListener,
            delButtonClickListener
        )
    }

    override fun onBindViewHolder(holder: RecentSearchViewHolder, pos: Int) {
        holder.bind(getItem(pos))
    }

    class RecentSearchViewHolder(
        private val binding: StoreItemRecentSearchedBinding,
        private val rootClickListener: (String) -> Unit,
        private val delButtonClickListener: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: RecentlySearchKeywordEntity) {
            with(binding){
                textviewRecentSearch.text = item.keyword

                root.setOnClickListener {
                    rootClickListener.invoke(item.keyword)
                }

                imageButtonDelete.setOnClickListener {
                    delButtonClickListener.invoke(item.id)
                }
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<RecentlySearchKeywordEntity>() {

            override fun areItemsTheSame(oldItem: RecentlySearchKeywordEntity, newItem: RecentlySearchKeywordEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RecentlySearchKeywordEntity, newItem: RecentlySearchKeywordEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}
