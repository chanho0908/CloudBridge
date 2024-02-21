package com.myproject.cloudbridge.view.search

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import com.myproject.cloudbridge.databinding.FragmentSearchBinding
import com.myproject.cloudbridge.view.main.MainActivity

class SearchFragment : Fragment() {
    private var _binding : FragmentSearchBinding ?= null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView(){
        with(binding) {

            materialToolbar.setNavigationOnClickListener {
                // 현재 Activity를 종료시킨다.
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }

            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}