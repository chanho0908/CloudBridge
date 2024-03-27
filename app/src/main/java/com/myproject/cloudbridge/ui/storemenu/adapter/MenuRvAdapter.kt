package com.myproject.cloudbridge.ui.storemenu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myproject.cloudbridge.databinding.StoreItemMenuBinding
import com.myproject.cloudbridge.model.store.StoreInfoSettingModel
import com.myproject.cloudbridge.model.store.StoreMenuRvModel

class MenuRvAdapter: ListAdapter<StoreMenuRvModel, MenuViewHolder>(DiffCallback){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder(StoreItemMenuBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val binding = holder.binding
        with(binding){
            menuName.text = getItem(position).productName
            menuCount.text = getItem(position).productQuantity.toString()

            Glide.with(binding.root)
                .load(getItem(position).imgBitmap)
                .into(menuImgView)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<StoreMenuRvModel>() {

            override fun areItemsTheSame(oldItem: StoreMenuRvModel, newItem: StoreMenuRvModel): Boolean {
                return oldItem.productName == newItem.productName
            }

            override fun areContentsTheSame(oldItem: StoreMenuRvModel, newItem: StoreMenuRvModel): Boolean {
                return oldItem == newItem

            }
        }
    }

}

class MenuViewHolder(val binding: StoreItemMenuBinding): RecyclerView.ViewHolder(binding.root)