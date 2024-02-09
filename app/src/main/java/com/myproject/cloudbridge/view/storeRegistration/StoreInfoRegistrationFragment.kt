package com.myproject.cloudbridge.view.storeRegistration

import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
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
import com.google.android.material.snackbar.Snackbar
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentStoreInfoRegistrationBinding
import com.myproject.cloudbridge.util.Constants
import com.myproject.cloudbridge.util.Constants.Companion.ADDR_RESULT
import com.myproject.cloudbridge.util.Constants.Companion.REQUEST_STORAGE_PERMISSIONS
import com.myproject.cloudbridge.util.Constants.Companion.absolutelyPath
import com.myproject.cloudbridge.util.Constants.Companion.isAllPermissionsGranted
import com.myproject.cloudbridge.util.Constants.Companion.makeStoreMainImage
import com.myproject.cloudbridge.util.Constants.Companion.requestPlzInputText
import com.myproject.cloudbridge.util.Constants.Companion.showSoftInput
import com.myproject.cloudbridge.view.intro.myStore.MyStoreActivity
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Locale


class StoreInfoRegistrationFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentStoreInfoRegistrationBinding ?= null
    private val binding: FragmentStoreInfoRegistrationBinding get() = _binding!!

    private lateinit var launcherForPermission: ActivityResultLauncher<Array<String>>
    private lateinit var launcherForActivity:  ActivityResultLauncher<Intent>

    private val viewModel: StoreManagementViewModel by viewModels()
    private var imgUrl: Uri? = null

    private val args: StoreInfoRegistrationFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStoreInfoRegistrationBinding.inflate(inflater, container, false)
        initAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initActivityProcess()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.flag.collectLatest{
                    if (it){
                        val intent = Intent(activity, MyStoreActivity::class.java)
                        intent.putExtra("FLAG", "REGISTER")

                        startActivity(intent)

                        // 부모 액티비티 종료
                        activity?.finish()
                    }
                }
            }
        }
    }

    private fun initAdapter(){
        val items = resources.getStringArray(R.array.category)
        val adapter = ArrayAdapter(requireActivity(), R.layout.array_list_item, items)
        binding.kindEdit.setAdapter(adapter)
    }

    private fun initView(){
        with(binding) {
            submitButton.setOnClickListener(this@StoreInfoRegistrationFragment)
            btnAddr.setOnClickListener(this@StoreInfoRegistrationFragment)
            imgLoadButton.setOnClickListener(this@StoreInfoRegistrationFragment)

            storeNameLayout.requestFocus()
            showSoftInput(requireActivity(), storeNameEdit)

            materialToolbar.setNavigationOnClickListener {
                val action = R.id.action_storeInfoRegistrationFragment_to_CPRFragment
                Navigation.findNavController(it).navigate(action)
            }

            storeNameEdit.addTextChangedListener {
                if (it.toString().isNotEmpty()) storeNameLayout.helperText = ""
                else storeNameLayout.helperText = "매장명을 입력해 주세요"
            }

            representativeNameEdit.addTextChangedListener {
                if (it.toString().isNotEmpty()) representativeNameLayout.helperText = ""
                else representativeNameLayout.helperText = "대표자명을 입력해 주세요."
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

    private fun initActivityProcess(){
        val contracts = ActivityResultContracts.RequestMultiplePermissions()
        launcherForPermission = registerForActivityResult(contracts){ permissions ->
            if (permissions.all { it.value }) {
                accessGallery()
            } else {
                // 하나 이상의 권한이 거부된 경우 처리할 작업
                permissions.forEach { (permission, isGranted) ->
                    when {
                        !isGranted -> {
                            // 사용자가 이전에 해당 권한을 거부하고, "다시 묻지 않음"을 선택한 경우에 false를 반환
                            if(!shouldShowRequestPermissionRationale(permission)){
                                // 사용자에게 왜 권한이 필요한지 설명하는 다이얼로그 또는 메시지를 표시
                                showPermissionSnackBar()
                            }
                        }
                        else -> {
                            // 사용자가 "다시 묻지 않음"을 선택한 경우 처리할 작업
                            showPermissionSnackBar()
                        }
                    }
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
                        imgUrl = callback.data

                        Glide.with(requireContext())
                            .load(imgUrl)
                            .fitCenter()
                            .apply(RequestOptions().override(800, 800))
                            .into(binding.mainImgView)
                    }
                }
        }

    }

    private fun accessGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "image/*"
        )
        launcherForActivity.launch(intent)
    }

    private fun showPermissionSnackBar() {
        Snackbar.make(binding.root, "권한이 거부 되었습니다. 설정(앱 정보)에서 권한을 확인해 주세요.",
            Snackbar.LENGTH_INDEFINITE).setAction("확인"){
            //설정 화면으로 이동
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val packageName = requireActivity().packageName
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }.show()
    }

    //주소로 위도,경도 구하는 GeoCoding
    private fun TranslateGeo(address: String): Location = try {
        val locations = Geocoder(requireContext(), Locale.KOREA).getFromLocationName(address, 1)
        if (!locations.isNullOrEmpty()) {
            Location("").apply {
                latitude = locations[0].latitude
                longitude = locations[0].longitude
            }
        } else {
            throw Exception("주소를 변환할 수 없습니다.")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // 예외 발생 시 빈 Location 객체를 반환
        Location("").apply {
            latitude = 0.0
            longitude = 0.0
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.submit_button -> {

                with(binding){

                    val storeName = storeNameEdit.text.toString()
                    val crn = args.bno
                    val ceoName = representativeNameEdit.text.toString()
                    val phone = phoneEdit.text.toString()
                    val addr = addrEdit.text.toString()
                    val kind = kindEdit.text.toString()

                    if (storeName.isEmpty()) {
                        requestPlzInputText("매장명을 입력해 주세요", storeNameLayout)
                    } else if (ceoName.isEmpty()) {
                        requestPlzInputText("점주명을 입력해 주세요", representativeNameLayout)
                    } else if (phone.isEmpty()) {
                        requestPlzInputText("매장 전화 번호를 입력해 주세요", phoneLayout)
                    } else if (addr.isEmpty()) {
                        requestPlzInputText("주소를 입력해 주세요", addrLayout)
                    } else if (imgUrl == null) {
                        Toast.makeText(requireContext(), "대표 사진을 등록해 주세요", Toast.LENGTH_LONG).show()
                    } else {
                        val location = TranslateGeo(addr)

                        val lat = location.latitude
                        val lng = location.longitude

                        if (lat == 0.0 || lng == 0.0) {
                            requestPlzInputText("올바른 주소를 입력해 주세요", addrLayout)
                        } else {
                            if (imgUrl != null) {

                                val imgBody = makeStoreMainImage(requireActivity(), imgUrl!!)

                                viewModel.registrationStore(imgBody, storeName, ceoName,
                                    crn, phone, addr, lat.toString(), lng.toString(), kind
                                )
                            }
                        }
                    }
                }
            }
            R.id.btnAddr -> {
                val intent = Intent(requireContext(), AddressActivity::class.java)
                launcherForActivity.launch(intent)
            }

            R.id.img_load_button -> {
                if (isAllPermissionsGranted(requireContext(), REQUEST_STORAGE_PERMISSIONS)) accessGallery()
                else launcherForPermission.launch(REQUEST_STORAGE_PERMISSIONS)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}