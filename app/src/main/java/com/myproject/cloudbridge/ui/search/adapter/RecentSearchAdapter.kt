package com.myproject.cloudbridge.ui.search.adapter

import android.util.Log
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
): ListAdapter<RecentlySearchKeywordEntity, RecentSearchViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        val binding = StoreItemRecentSearchedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentSearchViewHolder, pos: Int) {
        val binding = holder.binding
        with(binding){
            textviewRecentSearch.text = getItem(pos).keyword
            root.setOnClickListener { rootClickListener.invoke(getSelectedItemKeyword(pos)) }
            imageButtonDelete.setOnClickListener {
                Log.d("sdsdass", "onBindViewHolder pos $pos: item ${getSelectedItemId(pos)}")
                delButtonClickListener.invoke(getSelectedItemId(pos))
            }
        }
    }
    private fun getSelectedItemKeyword(pos: Int) = getItem(pos).keyword
    private fun getSelectedItemId(pos: Int) = getItem(pos).id
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
class RecentSearchViewHolder(val binding: StoreItemRecentSearchedBinding) : RecyclerView.ViewHolder(binding.root)