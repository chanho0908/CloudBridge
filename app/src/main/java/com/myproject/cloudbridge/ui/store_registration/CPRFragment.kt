package com.myproject.cloudbridge.ui.store_registration

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentCPRBinding
import com.myproject.cloudbridge.model.store.AllCrnResponseModel
import com.myproject.cloudbridge.util.hideSoftInput
import com.myproject.cloudbridge.util.setHelperTextGreen
import com.myproject.cloudbridge.util.setHelperTextGreenList
import com.myproject.cloudbridge.util.setHelperTextRed
import com.myproject.cloudbridge.util.setHelperTextRedList
import com.myproject.cloudbridge.util.showSoftInput
import com.myproject.cloudbridge.ui.mystore.NotRegistsedStoreActivity
import com.myproject.cloudbridge.viewmodel.StoreManagementViewModel
import kotlinx.coroutines.launch


class CPRFragment : Fragment() {
    private var _binding: FragmentCPRBinding ?= null
    private val binding : FragmentCPRBinding get() = _binding!!
    private val viewModel: StoreManagementViewModel by viewModels()

    private var result = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCPRBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView(){

        with(binding){

            cprLayout.requestFocus()
            requireContext().showSoftInput(cprEdit)

            materialToolbar.setNavigationOnClickListener {
                val intent = Intent(requireContext(), NotRegistsedStoreActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }

            submitBtn.setOnClickListener {
                val bno = cprEdit.text.toString()
                val action = CPRFragmentDirections.actionCPRFragmentToStoreInfoRegistrationFragment(bno)

                Navigation.findNavController(it).navigate(action)
            }

            cprEdit.addTextChangedListener{
                val input = it.toString()

                if (input.length < 10 || input.length > 10) setWarningBox("사업자 등록 번호는 10자리 입니다.")

                // 1. 사용자가 입력한 사업자 등록 번호가 10글자면
                if (input.length == 10){
                    // 2. 사업자등록번호 조회 API 호출
                    viewModel.getCRNState(binding.cprEdit.text.toString())

                    searchBtn.isClickable = true
                    cprLayout.helperText = ""
                    cprLayout.setStartIconDrawable(R.drawable.baseline_check_24)
                    cprLayout.setStartIconTintList(requireContext().setHelperTextGreenList())
                    cprLayout.boxStrokeColor = requireContext().setHelperTextGreen()
                }

            }

            //1208147521
            searchBtn.setOnClickListener {
                val input = cprEdit.text.toString()

                if (input.isEmpty()){
                    setWarningBox("사업자 등록 번호를 입력 하세요")
                }else{
                    // 1. 등록된 모든 사업자 등록번호를 가져온다.
                    viewModel.getCompanyRegistrationNumberList()

                    viewLifecycleOwner.lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.STARTED){

                            // 위에서 호출한 사업자 등록번호 조회 API의 결과값을 저장
                            viewModel.state.collect { crn ->
                                result = crn.data[0].tax_type
                            }
                        }
                    }

                    viewLifecycleOwner.lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.STARTED){
                            viewModel.crnList.collect{ crnList ->
                                // 이미 사용 중인 사업자 등록 번호일 때
                                if(crnList.contains(AllCrnResponseModel(cprEdit.text.toString()))){
                                    setWarningBox("이미 사용 중인 사업자 등록 번호 입니다.")
                                    // 존재 하지 않는 사업자 등록 번호일 때
                                    // 변경 금지 : 응답으로 오는 텍스트 그대로여야함
                                }else if (result == "국세청에 등록되지 않은 사업자등록번호입니다.") {
                                    setWarningBox("국세청에 등록되지 않은 사업자등록번호입니다.")
                                    // 사용 가능한 사업자 등록 번호일 때
                                } else {
                                    setPermittedBox()
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private fun setPermittedBox(){
        with(binding) {
            requireContext().hideSoftInput(cprEdit)
            cprLayout.setStartIconDrawable(R.drawable.baseline_check_24)
            cprLayout.setStartIconTintList(requireContext().setHelperTextGreenList())
            cprLayout.setHelperTextColor(ColorStateList.valueOf(requireContext().setHelperTextGreen()))
            cprLayout.boxStrokeColor = requireContext().setHelperTextGreen()
            cprLayout.helperText = "사용 가능한 사업자 등록 번호 입니다."
            submitBtn.visibility = View.VISIBLE
        }
    }

    private fun setWarningBox(helperText: String){
        with(binding) {
            cprLayout.setStartIconDrawable(R.drawable.baseline_priority_high_24)
            cprLayout.setStartIconTintList(requireContext().setHelperTextRedList())
            cprLayout.setHelperTextColor(ColorStateList.valueOf(requireContext().setHelperTextRed()))
            cprLayout.boxStrokeColor = requireContext().setHelperTextRed()
            cprLayout.helperText = helperText
            submitBtn.visibility = View.INVISIBLE
            searchBtn.isClickable = false
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}