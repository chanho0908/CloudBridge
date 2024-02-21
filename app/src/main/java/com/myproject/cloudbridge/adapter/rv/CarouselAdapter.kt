package com.myproject.cloudbridge.adapter.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myproject.cloudbridge.databinding.CarouselItemBinding
import com.myproject.cloudbridge.model.store.CarouselModel

class CarouselAdapter(private val dataList: ArrayList<CarouselModel>): RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>(){

    inner class CarouselViewHolder(val binding: CarouselItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        return CarouselViewHolder(CarouselItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val binding = holder.binding

        binding.carouselImageView.setImageResource(dataList[position].resource)
        //Glide.with(holder.binding.root).load(dataList[position].resource).into(binding.carouselImageView)
        binding.storeNameTextView.text = dataList[position].id

    }
}


