package com.myproject.cloudbridge.ui.mystore.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentUpdate1Binding
import com.myproject.cloudbridge.ext.setHelperTextGreen
import com.myproject.cloudbridge.ext.setHelperTextGreenList
import com.myproject.cloudbridge.ext.setHelperTextRed
import com.myproject.cloudbridge.ext.setHelperTextRedList
import com.myproject.cloudbridge.ext.showSoftInput
import com.myproject.cloudbridge.ui.mystore.vm.StoreManagementViewModel
import com.myproject.cloudbridge.ui.mystore.vm.StoreManagementViewModelFactory
import kotlinx.coroutines.launch

class UpdateFragment1 : Fragment() {
    private var _binding: FragmentUpdate1Binding? = null
    private val binding: FragmentUpdate1Binding
        get() = _binding!!

    private lateinit var viewModel: StoreManagementViewModel
    private var isSearched = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUpdate1Binding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // stateFlow 는 같은 값을 emit 하지 않는다.
                viewModel.isEqualCrn.collect {
                    if (it == true) {
                        val crn = binding.cprEdit.text.toString()
                        val action = UpdateFragment1Directions.actionUpdateFragment1ToUpdateFragment2(crn)
                        Navigation.findNavController(view).navigate(action)
                    } else {
                        if (isSearched) {
                            setWarningBox("등록된 정보가 일치 하지 않습니다.")
                        }
                    }
                }
            }
        }
    }

    private fun initView() {
        val viewModelFactory = StoreManagementViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory)[StoreManagementViewModel::class.java]
        with(binding) {

            cprLayout.requestFocus()
            requireContext().showSoftInput(cprEdit)

            materialToolbar.setNavigationOnClickListener {
                activity?.finish()
            }

            cprEdit.addTextChangedListener {
                val input = it.toString()

                if (input.length < 10 || input.length > 10) setWarningBox("사업자 등록번호는 10자리 입니다")
                else setPermittedBox()
            }

            searchButton.setOnClickListener {
                val input = binding.cprEdit.text.toString()

                if (input.length == 10) {
                    isSearched = true
                    viewModel.checkMyCompanyRegistrationNumber(input)

                } else {
                    setWarningBox("사업자 등록 번호를 입력 해주세요.")
                }
            }
        }
    }


    private fun setPermittedBox() {
        with(binding) {
            cprLayout.helperText = ""
            cprLayout.setStartIconDrawable(R.drawable.baseline_check_24)
            cprLayout.setStartIconTintList(requireContext().setHelperTextGreenList())
            cprLayout.boxStrokeColor = requireContext().setHelperTextGreen()
        }
    }

    private fun setWarningBox(helperText: String) {
        with(binding) {
            cprLayout.setStartIconDrawable(R.drawable.baseline_priority_high_24)
            cprLayout.setStartIconTintList(requireContext().setHelperTextRedList())
            cprLayout.setHelperTextColor(
                ColorStateList.valueOf(requireContext().setHelperTextRed())
            )
            cprLayout.boxStrokeColor = requireContext().setHelperTextRed()
            cprLayout.helperText = helperText
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}