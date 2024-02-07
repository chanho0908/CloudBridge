package com.myproject.cloudbridge.view.intro.myStore

import com.myproject.cloudbridge.util.Constants.Companion.ADDR_RESULT
import com.myproject.cloudbridge.util.Constants.Companion.REQUEST_STORAGE_PERMISSIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentUpdate2Binding
import com.myproject.cloudbridge.util.Constants
import com.myproject.cloudbridge.util.Constants.Companion.requestPlzInputText
import com.myproject.cloudbridge.view.storeRegistration.AddressActivity
import com.myproject.cloudbridge.viewModel.MyPageViewModel
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class UpdateFragment2 : Fragment(), View.OnClickListener {
    private var _binding: FragmentUpdate2Binding? = null
    private val binding get() = _binding!!
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val storeManagementViewModel: StoreManagementViewModel by viewModels()

    private var imgUrl: Uri ?= null

    private lateinit var launcherForPermission: ActivityResultLauncher<Array<String>>
    private lateinit var launcherForActivity: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update2, container, false)

        val items = resources.getStringArray(R.array.category)
        val adapter = ArrayAdapter(requireActivity(), R.layout.array_list_item, items)
        binding.kindEdit.setAdapter(adapter)

        binding.vm = myPageViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        myPageViewModel.getMyStoreInfo()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initActivityProcess()
    }

    private fun initView(){
        binding.apply{

            submitButton.setOnClickListener(this@UpdateFragment2)
            btnAddr.setOnClickListener(this@UpdateFragment2)
            imgLoadButton.setOnClickListener(this@UpdateFragment2)

            materialToolbar.setNavigationOnClickListener {
                val action = R.id.action_updateFragment2_to_updateFragment1
                Navigation.findNavController(it).navigate(action)
            }

            storeNameEdit.addTextChangedListener {
                if (it.toString().isNotEmpty()) storeNameLayout.helperText = ""
                else storeNameLayout.helperText = "매장명을 입력해 주세요"
            }

            ceoNameEdit.addTextChangedListener {
                if (it.toString().isNotEmpty()) representativeNameLayout.helperText = ""
                else representativeNameLayout.helperText = "대표자명을 입력해 주세요."
            }

            phoneEdit.addTextChangedListener( object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(!isNumeric(s.toString())) phoneLayout.helperText = "숫자만 입력해 주세요"

                }
                override fun afterTextChanged(s: Editable?) {
                    if (isNumeric(s.toString())) phoneLayout.helperText = ""
                }
            })

            addrEdit.addTextChangedListener {
                if (it != null) {
                    addrLayout.helperText = ""
                }
            }
        }
    }

    private fun initActivityProcess(){
        val contract1 = ActivityResultContracts.RequestMultiplePermissions()
        launcherForPermission = registerForActivityResult(contract1){ permissions ->
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

        val contract2 = ActivityResultContracts.StartActivityForResult()
        launcherForActivity = registerForActivityResult(contract2) { result ->
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
            Snackbar.LENGTH_INDEFINITE
        ).setAction("확인"){
            //설정 화면으로 이동
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val packageName = requireActivity().packageName
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }.show()
    }

    private fun isAllPermissionsGranted(): Boolean = REQUEST_STORAGE_PERMISSIONS.all { permission->
        ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    //주소로 위도,경도 구하는 GeoCoding
    fun translateGeo(address: String): Location = try {
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

    fun isNumeric(s: String): Boolean = try {
        s.toDouble()
        true
    } catch (e: NumberFormatException) {
        false
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.submit_button -> {
                binding.apply {
                    val storeName = storeNameEdit.text.toString()
                    val representativeName = ceoNameEdit.text.toString()
                    val phone = phoneEdit.text.toString()
                    val addr = addrEdit.text.toString()
                    val kind = kindEdit.text.toString()

                    if (storeName.isEmpty()) {
                        requestPlzInputText("매장명을 입력해 주세요", storeNameLayout)

                    } else if (representativeName.isEmpty()) {
                        requestPlzInputText("점주명을 입력해 주세요", representativeNameLayout)

                    } else if (phone.isEmpty()) {
                        requestPlzInputText("매장 전화 번호를 입력해 주세요", phoneLayout)

                    } else if (addr.isEmpty()) {
                        requestPlzInputText("주소를 입력해 주세요", addrLayout)

                    } else {
                        Toast.makeText(requireActivity(), "통과", Toast.LENGTH_SHORT).show()

                        // 코루틴을 사용하여 백그라운드 작업 수행
                        lifecycleScope.launch(Dispatchers.IO) {

                            // TranslateGeo와 같은 시간이 많이 소요되는 작업 수행
                            val location = translateGeo(addr)

                            val lat = location.latitude
                            val lng = location.longitude

                            if(lat == 0.0 || lng == 0.0){
                                withContext(Dispatchers.Main){
                                    requestPlzInputText("올바른 주소를 입력해 주세요", addrLayout)
                                }

                            }else{
                                storeManagementViewModel.updateMyStore(
                                    requireContext(), imgUrl, storeName, representativeName,
                                    phone, addr, lat.toString(), lng.toString(), kind)

                                startActivity(Intent(requireContext(), MyStoreActivity::class.java))

                                activity?.finish()
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
                if (isAllPermissionsGranted()) accessGallery()
                else launcherForPermission.launch(REQUEST_STORAGE_PERMISSIONS)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}