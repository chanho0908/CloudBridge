package com.myproject.cloudbridge.ui.search.fragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.myproject.cloudbridge.databinding.FragmentSearchBinding
import com.myproject.cloudbridge.datasource.local.entity.RecentlySearchKeywordEntity
import com.myproject.cloudbridge.ui.search.adapter.AutoCompleteKeywordAdapter
import com.myproject.cloudbridge.ui.search.adapter.RecentSearchAdapter
import com.myproject.cloudbridge.ui.search.vm.SearchViewModel
import com.myproject.cloudbridge.ext.showSoftInput
import kotlinx.coroutines.launch
import android.view.View.GONE
import android.view.View.VISIBLE
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.ext.repeatOnViewStarted
import com.myproject.cloudbridge.ui.search.vm.SearchViewModelFactory

class SearchFragment : Fragment(R.layout.fragment_search) {
    private val viewModel: SearchViewModel by activityViewModels{ SearchViewModelFactory() }
    private lateinit var mView: View

    private val recentSearchAdapter by lazy {
        RecentSearchAdapter(
            rootClickListener = { keyword ->
                moveFragment(keyword)
            },
            delButtonClickListener = { id ->
                viewModel.deleteKeyword(id)
            }
        )
    }

    private val autoCompleteSearchAdapter by lazy {
        AutoCompleteKeywordAdapter(
            itemClickListener = { keyword ->
                moveFragment(keyword)
            }
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentSearchBinding.bind(view)
        mView = view

        with(binding) {
            requireContext().showSoftInput(edittextSearch)
            edittextSearch.requestFocus()

            textinputlayoutSearch.setEndIconOnClickListener {
                val keyword = edittextSearch.text.toString().trim()
                if (keyword.isNotBlank()) {
                    viewModel.insertKeyword(RecentlySearchKeywordEntity(keyword))
                    moveFragment(keyword)
                } else {
                    Toast.makeText(requireContext(), "검색어를 입력해 주세요", Toast.LENGTH_SHORT).show()
                }
            }

            btnBack.setOnClickListener {
                activity?.finish()
            }

            textviewDeleteAll.setOnClickListener {
                showClearDialog()
            }

            edittextSearch.addTextChangedListener {
                val searchKeyword = it.toString().trim()

                viewModel.setSearchQuery(it.toString())

                repeatOnViewStarted {
                    viewModel.autoCompleteKeywordResult.collect { autoCompleteKeywords ->

                        linearlayoutSearchRecentSearches.visibility = GONE

                        if (autoCompleteKeywords.isNotEmpty()) {
                            autoCompleteSearchAdapter.submitList(autoCompleteKeywords)
                            rvAutoCompleteSearches.visibility = VISIBLE
                        }

                        if (autoCompleteKeywords.isEmpty()) {
                            rvAutoCompleteSearches.visibility = GONE
                            textviewNoSearch.visibility = VISIBLE
                        }
                    }

                }

                if (searchKeyword.isEmpty()) {
                    textviewNoSearch.visibility = GONE
                    rvAutoCompleteSearches.visibility = GONE

                    linearlayoutSearchRecentSearches.visibility = VISIBLE
                }
            }

            rvRecentSearches.adapter = recentSearchAdapter
            rvRecentSearches.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            (rvRecentSearches.layoutManager as LinearLayoutManager).stackFromEnd = true

            repeatOnViewStarted{
                viewModel.allKeyWord.collect {
                    recentSearchAdapter.submitList(it)
                }
            }

            rvAutoCompleteSearches.adapter = autoCompleteSearchAdapter
            rvAutoCompleteSearches.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        }
    }


    private fun moveFragment(keyword: String) {
        val action = SearchFragmentDirections.actionSearchFragmentToSearchResultFragment(keyword)
        Navigation.findNavController(mView).navigate(action)
    }

    private fun showClearDialog() {
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        with(materialAlertDialogBuilder) {
            setTitle("전체 삭제")
            setMessage("모든 최근 검색어를 삭제 하시겠습니까?")
            setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                viewModel.deleteAllKeyword()
            }
            setNegativeButton("취소") { _: DialogInterface, _: Int -> return@setNegativeButton }
            show()
        }

    }

}