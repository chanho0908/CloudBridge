package com.myproject.cloudbridge.view.intro.myStore

import android.R.attr
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentUpdate2Binding
import com.myproject.cloudbridge.util.Constants.Companion.ADDR_RESULT
import com.myproject.cloudbridge.util.Constants.Companion.makeStoreMainImage
import com.myproject.cloudbridge.util.Constants.Companion.requestPlzInputText
import com.myproject.cloudbridge.util.Constants.Companion.translateGeo
import com.myproject.cloudbridge.util.PermissionManagement
import com.myproject.cloudbridge.util.PermissionManagement.Companion.REQUEST_IMAGE_PERMISSIONS
import com.myproject.cloudbridge.view.storeRegistration.AddressActivity
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class UpdateFragment2 : Fragment(), View.OnClickListener {
    private var _binding: FragmentUpdate2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: StoreManagementViewModel by viewModels()

    private var imgUrl: Uri ?= null

    private lateinit var launcherForPermission: ActivityResultLauncher<Array<String>>
    private lateinit var launcherForActivity: ActivityResultLauncher<Intent>
    private lateinit var pm: PermissionManagement

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update2, container, false)

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.getMyStoreInfo()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initActivityProcess()

        viewLifecycleOwner.lifecycleScope.launch {
            // Activity가 포그라운드에 있을 때만 특정 라이프 사이클이 트리거 되어있을 때 동작
            // onStop 일 때 Job을 취소
            // https://kotlinworld.com/228
            repeatOnLifecycle(Lifecycle.State.STARTED){

                viewModel.flag.collectLatest{
                    if (it){
                        val intent = Intent(activity, MyStoreActivity::class.java)

                        startActivity(intent)

                        // 부모 액티비티 종료
                        activity?.finish()
                    }
                }
            }
        }


    }

    private fun initView(){
        pm = PermissionManagement(requireContext())
        val items = resources.getStringArray(R.array.category)
        val adapter = ArrayAdapter(requireActivity(), R.layout.array_list_item, items)
        binding.kindEdit.setAdapter(adapter)
    }

    private fun initListener(){
        with(binding){

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
                                pm.showPermissionSnackBar(binding.root)
                            }
                        }
                        else -> {
                            // 사용자가 "다시 묻지 않음"을 선택한 경우 처리할 작업
                            pm.showPermissionSnackBar(binding.root)
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
                        // ctrl alt l : 줄맞춤
                        // path나 uri를 삽입
                        imgUrl = callback.data

                        // 문제시 앱은 안죽는데 실행이 안된다. -> 메시지를 보내는 등
                        // require, assert 등등 앱을 죽여 문제를 발생시킴
                        // 상황에 따른 처리
                        val context = context ?: return@registerForActivityResult

                        val bitmap = if (Build.VERSION.SDK_INT < 28) {
                            MediaStore.Images.Media.getBitmap(
                                context.contentResolver,
                                imgUrl
                            )
                        } else {
                            val source: ImageDecoder.Source = ImageDecoder.createSource(
                                requireContext().contentResolver,
                                imgUrl!!
                            )
                            ImageDecoder.decodeBitmap(source)
                        }
                        viewModel.changeImage(bitmap)

//                        Glide.with(requireContext())
//                            .load(imgUrl)
//                            .fitCenter()
//                            .apply(RequestOptions().override(800, 800))
//                            .into(binding.mainImgView)
                    }
                }
        }
    }

    private fun getSavedStateInstance(){
        with(binding){
            viewModel.updateSavedData(
                storeName = storeNameEdit.text.toString(),
                contact = phoneEdit.text.toString(),
                ceoName = ceoNameEdit.text.toString(),
                address = addrEdit.text.toString(),
                kind = kindEdit.text.toString()
            )
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

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.submit_button -> {
                with(binding) {
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
                        val location = translateGeo(requireContext(), addr)

                        val lat = location.latitude
                        val lng = location.longitude

                        if(lat == 0.0 || lng == 0.0){
                            requestPlzInputText("올바른 주소를 입력해 주세요", addrLayout)
                        }else{
                            if (imgUrl != null) {

                                val imgBody = makeStoreMainImage(requireActivity(), imgUrl!!)

                                viewModel.updateMyStore(
                                    imgBody, storeName, representativeName,
                                    phone, addr, lat.toString(), lng.toString(), kind
                                )
                            }else{

                                viewModel.updateMyStore(
                                    null, storeName, representativeName,
                                    phone, addr, lat.toString(), lng.toString(), kind
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
                if (pm.isImagePermissionGranted()){
                    getSavedStateInstance()
                    accessGallery()
                }else{
                    launcherForPermission.launch(REQUEST_IMAGE_PERMISSIONS)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}