package com.myproject.cloudbridge.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.ui.adapter.rv.RecentSearchAdapter
import com.myproject.cloudbridge.databinding.FragmentSearchBinding
import com.myproject.cloudbridge.ui.main.MainActivity

class SearchFragment : Fragment() {
    private var _binding : FragmentSearchBinding?= null
    private val binding get() = _binding!!
    val recentSearchesList = mutableListOf<String>()

    private lateinit var mView: View
    private val recentSearchAdapter by lazy {
        RecentSearchAdapter(
            rootClickListener = { pos->
                showSearchResult(pos)
            },
            delButtonClickListener = { pos->
                recentSearchesList.removeAt(pos)
            }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        recentSearchesList.add("sdasda")
        recentSearchesList.add("sdasda2")
        recentSearchesList.add("sdasda3")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mView = view
        initView()
        initRv()
    }

    private fun initView(){
        with(binding) {
            toolbarSearch.setNavigationOnClickListener {
                // 현재 Activity를 종료시킨다.
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }


        }
    }

    private fun initRv(){
        with(binding.rvRecentSearches) {
            adapter = recentSearchAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
            (layoutManager as LinearLayoutManager).stackFromEnd = true
            recentSearchAdapter.submitList(recentSearchesList)
        }
    }

    private fun showSearchResult(pos: Int){
        val selectedItem = recentSearchesList[pos]
        binding.edittextSearch.setText(selectedItem)
        Thread.sleep(1000)
        val action = SearchFragmentDirections.actionSearchFragmentToSearchResultFragment(selectedItem)
        Navigation.findNavController(mView).navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}