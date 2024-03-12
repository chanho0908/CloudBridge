package com.myproject.cloudbridge.ui.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.myproject.cloudbridge.databinding.FragmentSearchResultBinding


class SearchResultFragment : Fragment() {
    private var _binding: FragmentSearchResultBinding? = null
    private val binding: FragmentSearchResultBinding get() = _binding!!
    private val args: SearchResultFragmentArgs by navArgs()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchResultBinding.inflate(layoutInflater)
        Log.d("dasdsad", "SearchResultFragment ${args.searchWord}")
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("dasdsad", "onDestroyView")
    }
}