package com.myproject.cloudbridge.ui.storemenu.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentMenuAddBinding
import com.myproject.cloudbridge.utility.Utils
import com.myproject.cloudbridge.utility.showErrorDialog
import com.myproject.cloudbridge.utility.showPermissionSnackBar
import java.io.InputStream

class MenuAddFragment : Fragment() {
    private var _binding: FragmentMenuAddBinding? = null
    private val binding: FragmentMenuAddBinding get() = _binding!!
    private lateinit var launcherForPermission: ActivityResultLauncher<Array<String>>
    private lateinit var launcherForActivity: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        _binding = FragmentMenuAddBinding.inflate(layoutInflater)
        settingLauncher()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(view)
    }

    private fun initToolbar(view: View){
        with(binding.toolbar){
            inflateMenu(R.menu.done_menu)

            setNavigationOnClickListener{
                val action = MenuAddFragmentDirections.actionMenuAddFragmentToMenuListFragment()
                Navigation.findNavController(view).navigate(action)
            }

            setOnMenuItemClickListener {
                when(it.itemId){

                    R.id.done_menu_item -> {
                        val menuName = binding.productNameEdit.text.toString()
                        val menuCnt = binding.productQuantityEdit.text.toString()
                        val menuInfo = binding.productIntroEdit.text.toString()

                        if (menuName.isEmpty()){
                            requireContext().showErrorDialog(binding.productIntroEdit, "메뉴명 미입력", "메뉴명을 입력해 주세요")
                        }else if (menuCnt.isEmpty()){
                            requireContext().showErrorDialog(binding.productIntroEdit, "수량 미입력", "메뉴 수량을 입력해 주세요")
                        }else{

                        }

                    }
                }
                true
            }
        }
    }

    private fun settingLauncher(){
        val contracts = ActivityResultContracts.RequestMultiplePermissions()
        launcherForPermission = registerForActivityResult(contracts){ permissions ->
            if (permissions.all { it.value }) {
                Utils.accessGallery(launcherForActivity)
            } else {
                // 하나 이상의 권한이 거부된 경우 처리할 작업
                permissions.forEach { (permission, isGranted) ->
                    when {
                        !isGranted -> {
                            // 사용자가 이전에 해당 권한을 거부하고, "다시 묻지 않음"을 선택한 경우에 false를 반환
                            if(!shouldShowRequestPermissionRationale(permission)){
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

        val contracts2 = ActivityResultContracts.StartActivityForResult()
        launcherForActivity = registerForActivityResult(contracts2) { result ->
            val callback = result.data
            if (callback != null){
                val selectedImageUri = callback.data

                val imageUri = Uri.parse(selectedImageUri.toString())
                val inputStream: InputStream? = requireContext().contentResolver.openInputStream(imageUri)
                val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)

                binding.menuImgView.setImageBitmap(bitmap)


            }
        }
    }
}