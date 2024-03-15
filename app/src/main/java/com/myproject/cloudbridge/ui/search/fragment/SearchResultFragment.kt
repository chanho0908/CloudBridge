package com.myproject.cloudbridge.ui.search.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.myproject.cloudbridge.databinding.FragmentSearchResultBinding


class SearchResultFragment : Fragment() {
    private var _binding: FragmentSearchResultBinding? = null
    private val binding: FragmentSearchResultBinding get() = _binding!!
    private lateinit var mView: View
    private val args: SearchResultFragmentArgs by navArgs()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchResultBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
    }

    private fun initView(){
        initToolbar()
    }

    private fun initToolbar(){
        with(binding){
            toolbarSearchResult.setNavigationOnClickListener{
                val action = SearchResultFragmentDirections.actionSearchResultFragmentToSearchFragment()
                Navigation.findNavController(mView).navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}