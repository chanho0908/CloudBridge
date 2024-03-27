package com.myproject.cloudbridge.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myproject.cloudbridge.databinding.StoreItemBinding
import com.myproject.cloudbridge.model.store.StoreInfoSettingModel

class StoreListAdapter : ListAdapter<StoreInfoSettingModel, MainViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<StoreInfoSettingModel>() {
            override fun areItemsTheSame(
                oldItem: StoreInfoSettingModel,
                newItem: StoreInfoSettingModel
            ): Boolean {
                return oldItem.storeInfo == newItem.storeInfo
            }

            override fun areContentsTheSame(
                oldItem: StoreInfoSettingModel,
                newItem: StoreInfoSettingModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            StoreItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val binding = holder.binding
        val item = getItem(position)
        val address = item.storeInfo.address
        val subAddress = address.substring(address.indexOf("도") + 1, address.length - 1)

        with(binding) {
            Glide.with(binding.root)
                .load(item.storeImage)
                .into(imageViewStoreImg)

            textviewStoreName.text = item.storeInfo.storeName
            textviewAddr.text = subAddress
        }
    }
}

class MainViewHolder(val binding: StoreItemBinding) : RecyclerView.ViewHolder(binding.root)