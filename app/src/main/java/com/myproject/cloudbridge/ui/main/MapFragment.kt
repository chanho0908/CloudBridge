package com.myproject.cloudbridge.ui.main

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentMapBinding
import com.myproject.cloudbridge.model.store.StoreInfoSettingModel
import com.myproject.cloudbridge.ui.search.SearchActivity
import com.myproject.cloudbridge.util.hasLocationPermission
import com.myproject.cloudbridge.util.locationProvider.FusedLocationProvider
import com.myproject.cloudbridge.util.locationProvider.OnLocationUpdateListener
import com.myproject.cloudbridge.util.showPermissionSnackBar
import com.myproject.cloudbridge.util.singleton.Utils.REQUEST_LOCATION_PERMISSIONS
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMapOptions

class MapFragment : Fragment(), OnLocationUpdateListener, OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding get() = _binding!!
    private lateinit var launcherForPermission: ActivityResultLauncher<Array<String>>
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private lateinit var allStoreData: ArrayList<StoreInfoSettingModel>
    private lateinit var nMap: NaverMap
    private val viewModel: StoreManagementViewModel by viewModels()

    private var currentLocation: LatLng = LatLng(35.1798159, 129.0750222)
    private lateinit var fusedLocationProvider: FusedLocationProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActivityProcess()
        initView(view)
    }

    private fun initView(view: View) {
        fusedLocationProvider = FusedLocationProvider(requireContext(), this)
        launcherForPermission.launch(REQUEST_LOCATION_PERMISSIONS)

        if (requireContext().hasLocationPermission()){
            Log.d("sdsadsa", "권한 허용")
            fusedLocationProvider.requestLastLocation()
            val options = NaverMapOptions()
                .camera(CameraPosition(currentLocation, 8.0))
                .mapType(NaverMap.MapType.Terrain)
            initMapInstance(options)
        }else{
            Log.d("sdsadsa", "권한 거부")
            val options = NaverMapOptions()
                .camera(CameraPosition(currentLocation, 8.0))
                .mapType(NaverMap.MapType.Terrain)
            initMapInstance(options)
        }


        with(binding) {
            viewLifecycleOwner.lifecycleScope.launch {
                progressBar.setProgressCompat(60, true)
                delay(700)
                progressBar.setProgressCompat(100, true)
            }

            searchView.setOnClickListener{
                startActivity(Intent(requireContext(), SearchActivity::class.java))
            }
        }

        //fetchAllStoreData()

        sheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.store_bottom_sheet))
        with(sheetBehavior) {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun initMapInstance(options: NaverMapOptions){
        val manger = childFragmentManager
        val mapFragment = manger.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                childFragmentManager.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    private fun initActivityProcess() {

        val contract = ActivityResultContracts.RequestMultiplePermissions()
        launcherForPermission = registerForActivityResult(contract) { permissions ->
            if (permissions.any { it.value }) {

                fusedLocationProvider.requestLastLocation()

            } else {
                permissions.entries.forEach { (_, isGranted) ->

                    when {
                        isGranted -> {
                            fusedLocationProvider.requestLastLocation()
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
                }
            }
        }
    }

    private fun fetchAllStoreData() {
        viewModel.fetchAllStoreFromRoom()
        viewLifecycleOwner.lifecycleScope.launch {

            // StateFlow는 동일한 값을 발행하지 않지만
            // LifeCycle이 트리거될때 한번 수행하므로 가져옴
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetch.collect { fetched ->
                    if (fetched == true) {
                        allStoreData = viewModel.allStoreData.value ?: ArrayList()
                        addMaker()
                    }
                }
            }
        }
    }

    private fun addMaker() {

        allStoreData.forEach { store ->


        }
    }

    override fun onLocationUpdated(location: Location) {
        Log.d("sdsadsa", "$location")
        currentLocation = LatLng(location.latitude, location.longitude)
        Log.d("sdsadsa", "currentLocation: $location")
    }

    override fun onStop() {
        super.onStop()
        fusedLocationProvider.stopLocationUpdates()
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        nMap = naverMap

        // https://navermaps.github.io/android-map-sdk/guide-ko/4-1.html
        // 지도 옵션 세팅
        with(naverMap){
            // 사용자 위치 따라가기
            locationTrackingMode = LocationTrackingMode.Follow

            // 건물 내부 표시
            isIndoorEnabled = true

            with(uiSettings){
                // 줌버튼
                isZoomControlEnabled = false

                // 실내지도 층 피커
                isIndoorLevelPickerEnabled = true

                // 축적바
                isScaleBarEnabled = true

                // 현위치 버튼
                isLocationButtonEnabled = true
            }
        }
    }


}