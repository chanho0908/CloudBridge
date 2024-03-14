package com.myproject.cloudbridge.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentStoreListBinding
import com.myproject.cloudbridge.ui.main.adapter.StoreListAdapter
import com.myproject.cloudbridge.ui.search.SearchActivity
import com.myproject.cloudbridge.ui.mystore.vm.StoreManagementViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StoreListFragment : Fragment() {
    private var _binding: FragmentStoreListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StoreManagementViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initRv()
        showRvData()
    }

    private fun initRv() {
        with(binding) {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.allStoreData.collect {
                        val adapter = StoreListAdapter()
                        adapter.submitList(it)
                        rv.layoutManager = GridLayoutManager(requireContext(), 2)
                        rv.adapter = adapter
                    }

                }
            }
        }
    }

    private fun showRvData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val fetched = viewModel.fetched.value
                Log.d("sdsdasdsa", fetched.toString())
                delay(3000)
                if (fetched != null) {
                    with(binding) {
                        if (fetched) {
                            shimmer.stopShimmer()
                            shimmer.visibility = View.GONE
                            rv.visibility = View.VISIBLE
                        } else {
                            shimmer.startShimmer()
                            shimmer.visibility = View.VISIBLE
                            rv.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun initToolbar() {
        with(binding.toolbar)  {
            inflateMenu(R.menu.search_menu)

            setOnMenuItemClickListener {
                when (it.itemId) {
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

