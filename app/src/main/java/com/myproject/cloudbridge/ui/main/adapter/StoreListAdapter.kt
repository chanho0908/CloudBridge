package com.myproject.cloudbridge.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myproject.cloudbridge.databinding.StoreItemBinding
import com.myproject.cloudbridge.model.store.StoreInfoSettingModel

class StoreListAdapter(
    private val itemClickListener: (String) -> Unit
) : ListAdapter<StoreInfoSettingModel, StoreListAdapter.MainViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MainViewHolder(StoreItemBinding.inflate(inflater, parent, false), itemClickListener)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MainViewHolder(private val  binding: StoreItemBinding, private val itemClickListener: (String) -> Unit) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: StoreInfoSettingModel){
            val address = item.storeInfo.address
            val subAddress = address.substring(address.indexOf("도") + 1, address.length - 1)

            with(binding) {
                root.setOnClickListener {
                    itemClickListener.invoke(item.storeInfo.crn)
                }
                Glide.with(binding.root)
                    .load(item.storeImage)
                    .into(imageViewStoreImg)

                textviewStoreName.text = item.storeInfo.storeName
                textviewAddr.text = subAddress
            }
        }
    }

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
}

