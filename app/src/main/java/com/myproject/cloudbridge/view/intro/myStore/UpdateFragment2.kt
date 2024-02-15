package com.myproject.cloudbridge.view.intro.myStore

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
import com.bumptech.glide.Glide
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentUpdate2Binding
import com.myproject.cloudbridge.util.singleton.Utils.ADDR_RESULT
import com.myproject.cloudbridge.util.singleton.Utils.REQUEST_IMAGE_PERMISSIONS
import com.myproject.cloudbridge.util.singleton.Utils.accessGallery
import com.myproject.cloudbridge.util.singleton.Utils.makeStoreMainImage
import com.myproject.cloudbridge.util.singleton.Utils.requestPlzInputText
import com.myproject.cloudbridge.util.singleton.Utils.translateGeo
import com.myproject.cloudbridge.util.management.hasImagePermission
import com.myproject.cloudbridge.util.management.showPermissionSnackBar
import com.myproject.cloudbridge.view.storeRegistration.AddressActivity
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class UpdateFragment2 : Fragment(), View.OnClickListener {
    private var _binding: FragmentUpdate2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: StoreManagementViewModel by viewModels()

    private var imgUrl: Uri? = null

    private lateinit var launcherForPermission: ActivityResultLauncher<Array<String>>
    private lateinit var launcherForActivity: ActivityResultLauncher<Intent>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUpdate2Binding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initActivityProcess()
    }

    private fun initView() {
        viewModel.getMyStoreInfo()

        with(binding) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.loading.collect {
                        if (it) {
                            viewModel.myStore.collect { myStore ->

                                storeNameEdit.setText(myStore.storeInfo.ceoName)
                                ceoNameEdit.setText(myStore.storeInfo.crn)
                                phoneEdit.setText(myStore.storeInfo.storeName)
                                addrEdit.setText(myStore.storeInfo.contact)

                                Glide.with(this@UpdateFragment2)
                                    .load(myStore.storeImage)
                                    .into(mainImgView)

                            }
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            // Activity가 포그라운드에 있을 때만 특정 라이프 사이클이 트리거 되어있을 때 동작
            // onStop 일 때 Job을 취소
            // https://kotlinworld.com/228
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.flag.collectLatest {
                    if (it) {
                        val intent = Intent(activity, MyStoreActivity::class.java)

                        startActivity(intent)

                        // 부모 액티비티 종료
                        activity?.finish()
                    }
                }
            }
        }

        val items = resources.getStringArray(R.array.category)
        val adapter = ArrayAdapter(requireContext(), R.layout.array_list_item, items)
        binding.kindEdit.setAdapter(adapter)
    }

    private fun initListener() {
        with(binding) {

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

    private fun initActivityProcess() {
        val contract1 = ActivityResultContracts.RequestMultiplePermissions()
        launcherForPermission = registerForActivityResult(contract1) { permissions ->
            if (permissions.any { it.value }) {
                accessGallery(launcherForActivity)
            } else {
                // 하나 이상의 권한이 거부된 경우 처리할 작업
                permissions.forEach { (permission, isGranted) ->
                    when {
                        !isGranted -> {
                            // 사용자가 이전에 해당 권한을 거부하고, "다시 묻지 않음"을 선택한 경우에 false를 반환
                            if (!shouldShowRequestPermissionRationale(permission)) {
                                // 사용자에게 왜 권한이 필요한지 설명하는 다이얼로그 또는 메시지를 표시
                                requireContext().showPermissionSnackBar(binding.root)
                            }
                        }

                        else -> {
                            // 사용자가 "다시 묻지 않음"을 선택한 경우 처리할 작업
                            requireContext().showPermissionSnackBar(binding.root)
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
                        //viewModel.changeImage(callback.data.toString())
                        if (imgUrl != null){
                            Glide.with(this@UpdateFragment2)
                                .load(imgUrl)
                                .into(binding.mainImgView)
                        }

                    }
                }
        }
    }

    private fun getSavedStateInstance() {
        with(binding) {
            viewModel.updateSavedData(
                storeName = storeNameEdit.text.toString(),
                contact = phoneEdit.text.toString(),
                ceoName = ceoNameEdit.text.toString(),
                address = addrEdit.text.toString(),
                kind = kindEdit.text.toString()
            )
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
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
                        val location = translateGeo(addr)

                        val lat = location.latitude
                        val lng = location.longitude

                        if (lat == 0.0 || lng == 0.0) {
                            requestPlzInputText("올바른 주소를 입력해 주세요", addrLayout)
                        } else {
                            if (imgUrl != null) {

                                val imgBody = makeStoreMainImage(imgUrl)

                                viewModel.updateMyStore(
                                    imgBody, storeName, representativeName,
                                    phone, addr, lat.toString(), lng.toString(), kind
                                )
                            } else {

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
                if (requireContext().hasImagePermission()) {
                    getSavedStateInstance()
                    accessGallery(launcherForActivity)
                } else {
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