package com.myproject.cloudbridge.adapter.rv.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myproject.cloudbridge.databinding.StoreItemBinding
import com.myproject.cloudbridge.db.entity.StoreEntity

class SelectStoreInfoAdapter(private val storeInfoList: List<StoreEntity>):
    RecyclerView.Adapter<SelectStoreInfoAdapter.MainViewHolder>() {

    override fun getItemCount(): Int = storeInfoList.size

    class MainViewHolder(val binding: StoreItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(StoreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val binding = holder.binding
        val bitmap =  storeInfoList[position].image

        binding.storeImg.setImageBitmap(bitmap)
        binding.storenameTv.text = storeInfoList[position].storeName
    }

}