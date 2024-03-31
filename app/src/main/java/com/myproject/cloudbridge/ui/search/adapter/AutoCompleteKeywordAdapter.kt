package com.myproject.cloudbridge.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myproject.cloudbridge.databinding.StoreItemAutoCompleteKeywordBinding

class AutoCompleteKeywordAdapter(
    private val itemClickListener: (String) -> Unit
): ListAdapter<String, AutoCompleteKeywordAdapter.AutoCompleteKeywordViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoCompleteKeywordViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return AutoCompleteKeywordViewHolder(
            StoreItemAutoCompleteKeywordBinding.inflate(inflater, parent, false),
            itemClickListener
        )
    }

    override fun onBindViewHolder(holder: AutoCompleteKeywordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AutoCompleteKeywordViewHolder(
        private val binding: StoreItemAutoCompleteKeywordBinding,
        private val itemClickListener: (String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            with(binding){
                textviewKeyword.text = item
                root.setOnClickListener {
                    itemClickListener.invoke(item)
                }
            }
        }
    }
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<String>() {

            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }
}