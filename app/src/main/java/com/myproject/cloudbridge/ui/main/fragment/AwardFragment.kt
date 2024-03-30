package com.myproject.cloudbridge.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.MultiBrowseCarouselStrategy
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.ui.main.adapter.CarouselAdapter
import com.myproject.cloudbridge.databinding.FragmentAwardBinding
import com.myproject.cloudbridge.model.store.CarouselModel

class AwardFragment : Fragment() {
    private var _binding: FragmentAwardBinding? = null
    private val binding: FragmentAwardBinding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAwardBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {

        val carouselModelList = arrayListOf(
            CarouselModel("FAT.P", R.drawable.carousel4),
            CarouselModel("작은 빵집", R.drawable.carousel1),
            CarouselModel("그러치 고로케", R.drawable.carousel2),
            CarouselModel("CAFE TOAST", R.drawable.carousel3)
        )
        val carouseRvAdapter = CarouselAdapter(carouselModelList)
        with(binding) {

            with(carouselRv) {

                val snapHelper = CarouselSnapHelper()
                snapHelper.attachToRecyclerView(this)
                adapter = carouseRvAdapter
                layoutManager = CarouselLayoutManager(MultiBrowseCarouselStrategy())

            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}