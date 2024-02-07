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
import com.myproject.cloudbridge.util.Constants.Companion.setHelperTextGreen
import com.myproject.cloudbridge.util.Constants.Companion.setHelperTextGreenList
import com.myproject.cloudbridge.util.Constants.Companion.setHelperTextRed
import com.myproject.cloudbridge.util.Constants.Companion.setHelperTextRedList
import com.myproject.cloudbridge.viewModel.MyPageViewModel
import kotlinx.coroutines.launch

class UpdateFragment1 : Fragment(), View.OnClickListener {
    private var _binding: FragmentUpdate1Binding? = null
    private val binding: FragmentUpdate1Binding
        get() = _binding!!

    private val viewModel: MyPageViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUpdate1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){
        val cprIs10 = resources.getString(R.string.cpr_is_10)
        val crnMax10 = resources.getString(R.string.crn_max_10).toInt()

        binding.apply{

            materialToolbar.setNavigationOnClickListener {
                startActivity(Intent(requireContext(), MyStoreActivity::class.java))
            }

            cprEdit.addTextChangedListener{
                val input = it.toString()

                if (input.length < crnMax10 || input.length > crnMax10) setWarningBox(cprIs10)
                else setPermittedBox()

            }

            //1208147521
            searchBtn.setOnClickListener(this@UpdateFragment1)
        }
    }

    private fun setPermittedBox(){
        binding.apply {
            cprLayout.helperText = ""
            cprLayout.setStartIconDrawable(R.drawable.baseline_check_24)
            cprLayout.setStartIconTintList(setHelperTextGreenList(requireContext()))
            cprLayout.boxStrokeColor = setHelperTextGreen(requireContext())
            searchBtn.isClickable = true
        }
    }

    private fun setWarningBox(helperText: String){
        binding.apply {
            cprLayout.setStartIconDrawable(R.drawable.baseline_priority_high_24)
            cprLayout.setStartIconTintList(setHelperTextRedList(requireContext()))
            cprLayout.setHelperTextColor(
                ColorStateList.valueOf(setHelperTextRed(requireContext())))
            cprLayout.boxStrokeColor = setHelperTextRed(requireContext())
            cprLayout.helperText = helperText
            searchBtn.isClickable = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        val isDifferCpr = resources.getString(R.string.is_differ_cpr)
        val cprIs10 = resources.getString(R.string.cpr_is_10)
        val crnMax10 = resources.getString(R.string.crn_max_10).toInt()

        when(v?.id){
            R.id.searchBtn -> {
                binding.apply {
                    if(cprEdit.length() == crnMax10){
                    val crn = cprEdit.text.toString()
                    viewModel.checkMyCrn(crn)

                    viewLifecycleOwner.lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.STARTED){
                            viewModel.isEqualCrn.collect{ isEqual->
                                if(isEqual){
                                    val action = UpdateFragment1Directions.actionUpdateFragment1ToUpdateFragment2()
                                    Navigation.findNavController(v).navigate(action)
                                }else{
                                    setWarningBox(isDifferCpr)
                                }
                            }
                        }
                    }
                  }
                }
            }
        }
    }
}