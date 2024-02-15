package com.myproject.cloudbridge.view.main

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentMapBinding
import com.myproject.cloudbridge.util.singleton.Utils.REQUEST_LOCATION_PERMISSIONS
import com.myproject.cloudbridge.util.management.hasLocationPermission
import com.myproject.cloudbridge.util.locationProvider.FusedLocationProvider
import com.myproject.cloudbridge.util.locationProvider.OnLocationUpdateListener
import com.myproject.cloudbridge.util.management.showPermissionSnackBar
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MapFragment : Fragment(), OnLocationUpdateListener, View.OnClickListener {
    private var _binding: FragmentMapBinding ?= null
    private val binding: FragmentMapBinding get() = _binding!!
    private lateinit var launcherForPermission: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationProvider: FusedLocationProvider
    private val viewModel: StoreManagementViewModel by viewModels()

    // MapCoordType.WGS84 을 나타내는 좌표 클래스
    // WGS84 : 경위도, GPS가 사용하는 좌표계
    private lateinit var latLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivityProcess()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){
        launcherForPermission.launch(REQUEST_LOCATION_PERMISSIONS)

        CoroutineScope(Dispatchers.Main).launch {
            binding.progressBar.setProgressCompat(60, true)
            delay(400)
            binding.progressBar.setProgressCompat(100, true)
        }

        binding.searchView.setOnClickListener(this)
    }

    private fun initActivityProcess(){
        fusedLocationProvider = FusedLocationProvider(requireContext(), this)

        val contract = ActivityResultContracts.RequestMultiplePermissions()
        launcherForPermission = registerForActivityResult(contract) { permissions ->
            if (permissions.all { it.value }) {

                fusedLocationProvider.requestLastLocation()

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
            showMapWithUserCurrentLocation()
        }
    }

    private fun showMapWithUserCurrentLocation(){
        with(binding) {
            // MapReadyCallback 을 통해 지도가 정상적으로 시작된 후에 수신할 수 있다.
            val KakaoMapReadyCallback = object : KakaoMapReadyCallback(){
                override fun onMapReady(kakaoMap: KakaoMap) {

                    with(kakaoMap){
                        // 나침반 설정
                        compass?.show()

                        if (!requireContext().hasLocationPermission()){
                            val seoulLatitude = 37.5665
                            val seoulLongitude = 126.9780
                            moveCamera(CameraUpdateFactory.newCenterPosition(LatLng.from(seoulLatitude, seoulLongitude)))
                        }else{
                            moveCamera(CameraUpdateFactory.newCenterPosition(latLng))
                            binding.locationButton.setOnClickListener {
                                if (requireContext().hasLocationPermission()){
                                    fusedLocationProvider.requestLastLocation()
                                    kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(latLng))
                                }else{
                                    requireContext().showPermissionSnackBar(binding.root)
                                }
                            }
                        }
                        makeMaker(this)
                    }
                }
            }

            // MapView : 지도가 보여지는 뷰
            val view = MapView(requireContext())

            mapView.addView(view)
            mapView.start(KakaoMapReadyCallback)
        }
    }

    private fun makeMaker(map: KakaoMap) {

        // 1. LabelStyles 설정하기 - Icon 이미지 하나만 있는 스타일
        val styles: LabelStyles = LabelStyles.from(LabelStyle.from(R.drawable.ic_bread_maker_64))

        viewModel.showAllStoreFromRoom()
        viewLifecycleOwner.lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.list.collect{
                    it.map { store->
                        latLng = LatLng.from(store.latitude.toDouble(), store.longitude.toDouble())

                        // 2. LabelOptions 생성하기
                        val options = LabelOptions.from(latLng)
                            .setStyles(styles)

                        map.labelManager?.lodLayer?.addLodLabel(options)
                    }
                }
            }
        }

        // 3. LodLabelLayer 가져오기 (또는 커스텀 LodLabelLayer 생성)

        // 4. LabelLayer 에 LabelOptions 을 넣어 Label 생성하기
    }

    override fun onLocationUpdated(location: Location) {
        latLng = LatLng.from(location.latitude, location.longitude)
    }

    override fun onStop() {
        super.onStop()
        fusedLocationProvider.stopLocationUpdates()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.search_view -> {
                startActivity(Intent(requireContext(), SearchActivity::class.java))
            }
        }
    }
}