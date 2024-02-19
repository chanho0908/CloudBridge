package com.myproject.cloudbridge.view.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.carousel.MultiBrowseCarouselStrategy
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.adapter.rv.CarouselAdapter
import com.myproject.cloudbridge.adapter.rv.SelectStoreInfoAdapter
import com.myproject.cloudbridge.databinding.FragmentStoreListBinding
import com.myproject.cloudbridge.model.store.CarouselModel
import com.myproject.cloudbridge.util.ImgResizing
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import kotlinx.coroutines.launch

class StoreListFragment : Fragment() {
    private var _binding: FragmentStoreListBinding ?= null
    private val binding get() = _binding!!
    private val viewModel: StoreManagementViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStoreListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initToolbar()
        initRv()
    }

    private fun initViewModel(){
        viewModel.fromServerToRoomSetAllStoreList()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.flag.collect {
                    if (it) {
                        viewModel.showAllStoreFromRoom()
                    }
                }
            }
        }
    }

    private fun initRv(){
        val carouseRvAdapter = CarouselAdapter(getCarouselModelList())

        with(binding){

            with(carouselRv){

                val snapHelper = CarouselSnapHelper()
                snapHelper.attachToRecyclerView(this)
                adapter = carouseRvAdapter
                layoutManager = CarouselLayoutManager(MultiBrowseCarouselStrategy())

            }

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.allStoreList.collect{ list->
                        val adapter = SelectStoreInfoAdapter(list)
                        rv.layoutManager = LinearLayoutManager(requireContext())
                        rv.adapter =  adapter
                    }
                }
            }
        }
    }

    private fun getCarouselModelList(): ArrayList<CarouselModel>{
        // 새 이미지 크기를 지정합니다.
        val newWidth = 650
        val newHeight = 850

        val imgResizing = ImgResizing()
        // 이미지를 리사이징합니다.

        val carouselModelList = arrayListOf(
            CarouselModel("carousel1", imgResizing.resizeImage(requireContext(), R.drawable.carousel1, newWidth, newHeight)),
            CarouselModel("carousel2", imgResizing.resizeImage(requireContext(), R.drawable.carousel2, newWidth, newHeight)),
            CarouselModel("carousel3", imgResizing.resizeImage(requireContext(), R.drawable.carousel3, newWidth, newHeight)),
            CarouselModel("carousel4", imgResizing.resizeImage(requireContext(), R.drawable.carousel4, newWidth, newHeight))

        )

        return carouselModelList
    }

    private fun initToolbar(){
        binding.toolbar.apply {
            inflateMenu(R.menu.search_menu)

            setOnMenuItemClickListener{
                when(it.itemId){
                    R.id.search -> {
                        startActivity(Intent(requireContext(), SearchActivity::class.java))
                        true
                    }
                    else -> true
                }
            }
        }
    }

}