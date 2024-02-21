package com.myproject.cloudbridge.adapter.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myproject.cloudbridge.databinding.StoreItemBinding
import com.myproject.cloudbridge.localDB.entity.StoreEntity
import com.myproject.cloudbridge.model.store.StoreInfoSettingModel

class StoreListAdapter: ListAdapter<StoreInfoSettingModel, StoreListAdapter.MainViewHolder>(DiffCallback){

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<StoreInfoSettingModel>(){
            override fun areItemsTheSame(oldItem: StoreInfoSettingModel, newItem: StoreInfoSettingModel): Boolean {
                return oldItem.storeInfo == newItem.storeInfo
            }

            override fun areContentsTheSame(oldItem: StoreInfoSettingModel, newItem: StoreInfoSettingModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    class MainViewHolder(val binding: StoreItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(StoreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val binding = holder.binding
        val item = getItem(position)

        with(binding){
            Glide.with(binding.root)
                .load(item.storeImage)
                .into(binding.storeImg)

            storename.text = item.storeInfo.storeName
        }


    }
}