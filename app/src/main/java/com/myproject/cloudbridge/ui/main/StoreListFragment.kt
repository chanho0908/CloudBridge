package com.myproject.cloudbridge.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.adapter.rv.StoreListAdapter
import com.myproject.cloudbridge.databinding.FragmentStoreListBinding
import com.myproject.cloudbridge.ui.search.SearchActivity
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import kotlinx.coroutines.launch

class StoreListFragment : Fragment() {
    private var _binding: FragmentStoreListBinding ?= null
    private val binding get() = _binding!!
    private val viewModel: StoreManagementViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStoreListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initRv()
    }

    private fun initRv(){
        with(binding){
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.allStoreData.collect{
                        val adapter = StoreListAdapter()
                        adapter.submitList(it)
                        rv.layoutManager = LinearLayoutManager(requireContext())
                        rv.adapter =  adapter
                    }

                }
            }
        }
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