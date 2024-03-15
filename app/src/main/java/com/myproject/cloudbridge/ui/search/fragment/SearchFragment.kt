package com.myproject.cloudbridge.ui.search.fragment

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.myproject.cloudbridge.databinding.FragmentSearchBinding
import com.myproject.cloudbridge.datasource.local.entity.RecentlySearchKeywordEntity
import com.myproject.cloudbridge.ui.main.MainActivity
import com.myproject.cloudbridge.ui.search.RecentSearchAdapter
import com.myproject.cloudbridge.ui.search.vm.SearchViewModel
import com.myproject.cloudbridge.utility.showSoftInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private var _binding : FragmentSearchBinding?= null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var mView: View

    private val recentSearchAdapter by lazy {
        RecentSearchAdapter(
            rootClickListener = { keyword->
                moveFragment(keyword)
            },
            delButtonClickListener = { keyword ->
                viewModel.deleteKeyword(keyword)
            }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        initView()
        initRv()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mView = view
    }

    private fun initView(){
        with(binding) {
            requireContext().showSoftInput(edittextSearch)
            toolbarSearch.setNavigationOnClickListener {
                // 현재 Activity를 종료시킨다.
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }

            textinputlayoutSearch.setEndIconOnClickListener {
                insertKeyword()
            }

            edittextSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    insertKeyword()
                    true
                } else {
                    false
                }
            }

            textviewDeleteAll.setOnClickListener {
                showClearDialog()
            }
        }
    }

    private fun insertKeyword(){
        val keyword = binding.edittextSearch.text.toString().trim()
        if (keyword.isNotBlank()){
            viewModel.insertKeyword(RecentlySearchKeywordEntity(keyword))
            //moveFragment(keyword)
        }else{
            Toast.makeText(requireContext(), "검색어를 입력해 주세요", Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveFragment(keyword :String ?= null){
        val action = if (keyword != null){
            SearchFragmentDirections.actionSearchFragmentToSearchResultFragment(keyword)
        }else{
            SearchFragmentDirections.actionSearchFragmentToSearchResultFragment()
        }

        Thread.sleep(300)
        Navigation.findNavController(mView).navigate(action)
    }
    private fun initRv(){
        with(binding.rvRecentSearches) {
            adapter = recentSearchAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
            (layoutManager as LinearLayoutManager).stackFromEnd = true
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.allKeyWord.collect{
                        recentSearchAdapter.submitList(it)
                    }

                }
            }
        }
    }

    // 입력 요소가 비어있을때 보여줄 다이얼로그를 구성하는 메서드
    fun showClearDialog(){
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        materialAlertDialogBuilder.setTitle("전체 삭제")
        materialAlertDialogBuilder.setMessage("모든 최근 검색어를 삭제 하시겠습니까?")
        materialAlertDialogBuilder.setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
            viewModel.deleteAllKeyword()
        }
        materialAlertDialogBuilder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}