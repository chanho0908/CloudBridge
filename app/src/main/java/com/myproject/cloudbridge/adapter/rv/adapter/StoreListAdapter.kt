package com.myproject.cloudbridge.adapter.rv.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myproject.cloudbridge.databinding.StoreItemBinding
import com.myproject.cloudbridge.db.entity.StoreEntity

class StoreListAdapter: ListAdapter<StoreEntity, StoreListAdapter.MainViewHolder>(DiffCallback){

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<StoreEntity>(){
            override fun areItemsTheSame(oldItem: StoreEntity, newItem: StoreEntity): Boolean {
                return oldItem.crn == newItem.crn
            }

            override fun areContentsTheSame(oldItem: StoreEntity, newItem: StoreEntity): Boolean {
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

        binding.storeImg.setImageURI(Uri.parse(getItem(position).imagePath))
        binding.storenameTv.text = getItem(position).storeName


    }


}