package com.myproject.cloudbridge.view.intro.myStore


import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentUpdate1Binding
import com.myproject.cloudbridge.util.Constants
import com.myproject.cloudbridge.util.Constants.Companion.setHelperTextGreen
import com.myproject.cloudbridge.util.Constants.Companion.setHelperTextGreenList
import com.myproject.cloudbridge.util.Constants.Companion.setHelperTextRed
import com.myproject.cloudbridge.util.Constants.Companion.setHelperTextRedList
import com.myproject.cloudbridge.util.Constants.Companion.showSoftInput
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UpdateFragment1 : Fragment(){
    private var _binding: FragmentUpdate1Binding? = null
    private val binding: FragmentUpdate1Binding
        get() = _binding!!

    private val viewModel: StoreManagementViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUpdate1Binding.inflate(inflater, container, false)
        return binding.root
    }
    private var isSearched = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                // stateFlow 는 같은 값을 emit 하지 않는다.
                viewModel.isEqualCrn.collectLatest {
                    if (it == true){
                        val action = UpdateFragment1Directions.actionUpdateFragment1ToUpdateFragment2()
                        Navigation.findNavController(view).navigate(action)
                    }
                    else {
                        Log.d("Sdasd","달라요")
                        Log.d("Sdasd","$isSearched")
                        if (isSearched){
                            Log.d("Sdasd","검색됐어요")
                            setWarningBox("등록된 정보가 일치 하지 않습니다.")
                        }
                    }
                }
            }
        }
    }

    private fun initView(){

        with(binding){

            cprLayout.requestFocus()
            showSoftInput(requireActivity(), cprEdit)

            materialToolbar.setNavigationOnClickListener {
                activity?.finish()
            }

            cprEdit.addTextChangedListener{
                val input = it.toString()

                if (input.length < 10 || input.length > 10) setWarningBox("사업자 등록번호는 10자리 입니다")
                else setPermittedBox()

            }

            searchButton.setOnClickListener{
                val input = binding.cprEdit.text.toString()

                if(input.length == 10){
                    isSearched = true
                    viewModel.checkMyCompanyRegistrationNumber(input)

                    Log.d("Sdasd","searchButton $isSearched")
                }else{
                    setWarningBox("사업자 등록 번호를 입력 해주세요.")
                }
            }

        }
    }


    private fun setPermittedBox(){
        with(binding) {
            cprLayout.helperText = ""
            cprLayout.setStartIconDrawable(R.drawable.baseline_check_24)
            cprLayout.setStartIconTintList(setHelperTextGreenList(requireContext()))
            cprLayout.boxStrokeColor = setHelperTextGreen(requireContext())
        }
    }

    private fun setWarningBox(helperText: String){
        with(binding) {
            cprLayout.setStartIconDrawable(R.drawable.baseline_priority_high_24)
            cprLayout.setStartIconTintList(setHelperTextRedList(requireContext()))
            cprLayout.setHelperTextColor(
                ColorStateList.valueOf(setHelperTextRed(requireContext())))
            cprLayout.boxStrokeColor = setHelperTextRed(requireContext())
            cprLayout.helperText = helperText
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}