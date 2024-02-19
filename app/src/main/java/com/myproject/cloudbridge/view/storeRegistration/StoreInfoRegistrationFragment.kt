package com.myproject.cloudbridge.view.storeRegistration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentStoreInfoRegistrationBinding
import com.myproject.cloudbridge.view.intro.myStore.MyStoreActivity
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.myproject.cloudbridge.util.singleton.Utils.ADDR_RESULT
import com.myproject.cloudbridge.util.singleton.Utils.REQUEST_IMAGE_PERMISSIONS
import com.myproject.cloudbridge.util.singleton.Utils.accessGallery
import com.myproject.cloudbridge.util.singleton.Utils.makeStoreMainImage
import com.myproject.cloudbridge.util.singleton.Utils.requestPlzInputText
import com.myproject.cloudbridge.util.singleton.Utils.showSoftInput
import com.myproject.cloudbridge.util.singleton.Utils.translateGeo
import com.myproject.cloudbridge.util.hasImagePermission
import com.myproject.cloudbridge.util.showPermissionSnackBar

class StoreInfoRegistrationFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentStoreInfoRegistrationBinding? = null
    private val binding: FragmentStoreInfoRegistrationBinding get() = _binding!!

    private lateinit var launcherForPermission: ActivityResultLauncher<Array<String>>
    private lateinit var launcherForActivity: ActivityResultLauncher<Intent>

    private val viewModel: StoreManagementViewModel by viewModels()
    private var imgUrl: Uri = Uri.parse("")

    private val args: StoreInfoRegistrationFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStoreInfoRegistrationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActivityProcess()
        initListener()
        initObserveSate()
    }

    private fun initObserveSate(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.flag.collect {
                    if (it) {
                        val intent = Intent(activity, MyStoreActivity::class.java)
                        intent.putExtra("FLAG", "REGISTER")
                        intent.putExtra("crn", args.bno)
                        startActivity(intent)

                        // 부모 액티비티 종료
                        activity?.finish()
                    }
                }
            }
        }
    }


    private fun initListener() {
        with(binding) {
            submitButton.setOnClickListener(this@StoreInfoRegistrationFragment)
            btnAddr.setOnClickListener(this@StoreInfoRegistrationFragment)
            imgLoadButton.setOnClickListener(this@StoreInfoRegistrationFragment)

            storeNameLayout.requestFocus()
            showSoftInput(storeNameEdit)

            materialToolbar.setNavigationOnClickListener {
                val action = R.id.action_storeInfoRegistrationFragment_to_CPRFragment
                Navigation.findNavController(it).navigate(action)
            }

            storeNameEdit.addTextChangedListener {
                if (it.toString().isNotEmpty()) storeNameLayout.helperText = ""
                else storeNameLayout.helperText = "매장명을 입력해 주세요"
            }

            ceoNameEdit.addTextChangedListener {
                if (it.toString().isNotEmpty()) ceoNameLayout.helperText = ""
                else ceoNameLayout.helperText = "대표자명을 입력해 주세요."
            }

            phoneEdit.addTextChangedListener {
                if (it.toString().isNotEmpty()) phoneLayout.helperText = ""
                else phoneLayout.helperText = "전화번호를 입력해 주세요"
            }

            addrEdit.addTextChangedListener {
                if (it.toString().isNotEmpty()) addrLayout.helperText = ""
                else phoneLayout.helperText = "주소를 입력해 주세요"
            }
        }
    }

    private fun initActivityProcess() {
        val contracts = ActivityResultContracts.RequestMultiplePermissions()

        launcherForPermission = registerForActivityResult(contracts) { permissions ->
            if (permissions.any { it.value }) {
                accessGallery(launcherForActivity)
            } else {
                // 하나 이상의 권한이 거부된 경우 처리할 작업
                //  ActivityCompat.shouldShowRequestPermissionRationale
                //  → 사용자가 권한 요청을 명시적으로 거부한 경우 true를 반환한다.
                //	→ 사용자가 다시 묻지 않음 선택한 경우 false를 반환한다.
                permissions.entries.forEach { (permission, isGranted) ->

                    when {
                        isGranted -> {
                            // 권한이 승인된 경우 처리할 작업
                            accessGallery(launcherForActivity)
                        }
                        !isGranted -> {
                            // 권한이 거부된 경우 처리할 작업
                            // 사용자에게 왜 권한이 필요한지 설명하는 다이얼로그 또는 메시지를 표시
                            requireContext().showPermissionSnackBar(binding.root)
                        }
                        else -> {
                            // 사용자가 "다시 묻지 않음"을 선택한 경우 처리할 작업
                            // 사용자에게 왜 권한이 필요한지 설명하는 다이얼로그 또는 메시지를 표시
                            requireContext().showPermissionSnackBar(binding.root)
                        }
                    }

                    //val context: Context = context ?: return@registerForActivityResult
                }
            }
        }

        val contracts2 = ActivityResultContracts.StartActivityForResult()
        launcherForActivity = registerForActivityResult(contracts2) { result ->
            val callback = result.data
            if (callback != null)
                when (result.resultCode) {
                    ADDR_RESULT -> {
                        val data = callback.getStringExtra("data")
                        binding.addrEdit.setText(data)
                    }
                    else -> {
                        imgUrl = callback.data ?: Uri.parse("")

                        Glide.with(requireContext())
                            .load(imgUrl)
                            .fitCenter()
                            .apply(RequestOptions().override(700, 700))
                            .into(binding.mainImgView)

                        binding.RequestImageTextView.visibility = View.INVISIBLE
                    }
                }
        }

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.submit_button -> {

                with(binding) {

                    val storeName = storeNameEdit.text.toString()
                    val crn = args.bno
                    val ceoName = ceoNameEdit.text.toString()
                    val phone = phoneEdit.text.toString()
                    val addr = addrEdit.text.toString()
                    val kind = kindEdit.text.toString()

                    if (storeName.isEmpty()) {
                        requestPlzInputText("매장명을 입력해 주세요", storeNameLayout)
                        storeNameLayout.requestFocus()
                    } else if (ceoName.isEmpty()) {
                        requestPlzInputText("점주명을 입력해 주세요", ceoNameLayout)
                        ceoNameLayout.requestFocus()
                    } else if (phone.isEmpty()) {
                        requestPlzInputText("매장 전화 번호를 입력해 주세요", phoneLayout)
                        phoneLayout.requestFocus()
                    } else if (addr.isEmpty()) {
                        requestPlzInputText("주소를 입력해 주세요", addrLayout)
                        addrLayout.requestFocus()
                    } else if (imgUrl == Uri.parse("")) {
                        binding.RequestImageTextView.visibility = View.VISIBLE
                    } else {
                        val location = translateGeo(addr)

                        val lat = location.latitude
                        val lng = location.longitude

                        if (lat == 0.0 || lng == 0.0) {
                            requestPlzInputText("올바른 주소를 입력해 주세요", addrLayout)
                        } else {
                            viewModel.registrationStore(imgUrl, storeName, ceoName,
                                crn, phone, addr, lat.toString(), lng.toString(), kind
                            )
                        }
                    }
                }
            }

            R.id.btnAddr -> {
                val intent = Intent(requireContext(), AddressActivity::class.java)
                launcherForActivity.launch(intent)
            }

            R.id.img_load_button -> {
                if (requireContext().hasImagePermission()) accessGallery(launcherForActivity)
                else launcherForPermission.launch(REQUEST_IMAGE_PERMISSIONS)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}