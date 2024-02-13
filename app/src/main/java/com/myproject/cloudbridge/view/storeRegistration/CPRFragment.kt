package com.myproject.cloudbridge.view.storeRegistration

import android.content.Context
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
import com.myproject.cloudbridge.util.Utils.hideSoftInput
import com.myproject.cloudbridge.util.Utils.setHelperTextGreen
import com.myproject.cloudbridge.util.Utils.setHelperTextGreenList
import com.myproject.cloudbridge.util.Utils.setHelperTextRed
import com.myproject.cloudbridge.util.Utils.setHelperTextRedList
import com.myproject.cloudbridge.util.Utils.showSoftInput
import com.myproject.cloudbridge.view.intro.myPage.NotRegistsedStoreActivity
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import kotlinx.coroutines.launch


class CPRFragment : Fragment() {
    private var _binding: FragmentCPRBinding ?= null
    private val binding : FragmentCPRBinding get() = _binding!!
    private val viewModel: StoreManagementViewModel by viewModels()

    private var result = ""
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCPRBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView(){
        val notRegisterCrn = resources.getString(R.string.not_registed_cpr)
        val usedRegisterCrn = resources.getString(R.string.used_registed_cpr)
        val useAbleCrn = resources.getString(R.string.useable_cpr)
        val cprIs10 = resources.getString(R.string.cpr_is_10)
        val crnMax10 = resources.getString(R.string.crn_max_10).toInt()

        with(binding){

            cprLayout.requestFocus()
            showSoftInput(cprEdit)

            materialToolbar.setNavigationOnClickListener {
                val intent = Intent(mContext, NotRegistsedStoreActivity::class.java)
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

                if (input.length < crnMax10 || input.length > crnMax10) setWarningBox(cprIs10)

                // 1. 사용자가 입력한 사업자 등록 번호가 10글자면
                if (input.length == 10){
                    // 2. 사업자등록번호 조회 API 호출
                    viewModel.getCRNState(binding.cprEdit.text.toString())

                    searchBtn.isClickable = true
                    cprLayout.helperText = ""
                    cprLayout.setStartIconDrawable(R.drawable.baseline_check_24)
                    cprLayout.setStartIconTintList(setHelperTextGreenList())
                    cprLayout.boxStrokeColor = setHelperTextGreen()
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
                                    setWarningBox(usedRegisterCrn)
                                    // 존재 하지 않는 사업자 등록 번호일 때
                                }else if (result == notRegisterCrn) {
                                    setWarningBox(notRegisterCrn)
                                    // 사용 가능한 사업자 등록 번호일 때
                                } else {
                                    setPermittedBox(useAbleCrn)
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private fun setPermittedBox(helperText: String){
        with(binding) {
            hideSoftInput(cprEdit)

            cprLayout.setStartIconDrawable(R.drawable.baseline_check_24)
            cprLayout.setStartIconTintList(setHelperTextGreenList())
            cprLayout.setHelperTextColor(ColorStateList.valueOf(setHelperTextGreen()))
            cprLayout.boxStrokeColor = setHelperTextGreen()
            cprLayout.helperText = helperText
            submitBtn.visibility = View.VISIBLE
        }
    }

    private fun setWarningBox(helperText: String){
        with(binding) {
            cprLayout.setStartIconDrawable(R.drawable.baseline_priority_high_24)
            cprLayout.setStartIconTintList(setHelperTextRedList())
            cprLayout.setHelperTextColor(ColorStateList.valueOf(setHelperTextRed()))
            cprLayout.boxStrokeColor = setHelperTextRed()
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