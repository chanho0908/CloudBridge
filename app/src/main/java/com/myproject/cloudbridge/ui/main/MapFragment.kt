package com.myproject.cloudbridge.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentMapBinding
import com.myproject.cloudbridge.model.store.StoreInfoSettingModel
import com.myproject.cloudbridge.ui.search.SearchActivity
import com.myproject.cloudbridge.util.singleton.Utils.LOCATION_PERMISSION_REQUEST_CODE
import com.myproject.cloudbridge.util.singleton.Utils.REQUEST_LOCATION_PERMISSIONS
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource

class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding get() = _binding!!
    // 내장 위치 추적 기능 사용
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationSource: FusedLocationSource
    private lateinit var allStoreData: ArrayList<StoreInfoSettingModel>
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private lateinit var naverMap: NaverMap
    private val viewModel: StoreManagementViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        fetchAllStoreData()
        initMap()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        sheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.store_bottom_sheet))
        with(sheetBehavior) {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
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
    }

    private fun initMap(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // LocationSource 구현체를 지정
        // 네이버 지도 SDK에 위치를 제공하는 인터페이스
        mLocationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        // 네이버맵 동적으로 불러오기
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    // 네이버맵 불러오기가 완료되면 콜백
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionDeniedMapUiSetting()
        }else {
            permissionGrantedMapUiSetting()

            // 사용자 현재 위치 받아오기
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                val latitude = location?.latitude ?: 35.1798159
                val longitude = location?.longitude ?: 129.0750222

                // 위치 오버레이의 가시성은 기본적으로 false로 지정되어 있습니다. 가시성을 true로 변경하면 지도에 위치 오버레이가 나타납니다.
                // 파랑색 점, 현재 위치 표시
                with(naverMap.locationOverlay) {
                    isVisible = true
                    position = LatLng(latitude, longitude)
                }

                // 카메라 현재 위치로 이동
                val cameraUpdate = CameraUpdate.scrollTo(
                    LatLng(
                        latitude,
                        longitude
                    )
                )
                naverMap.moveCamera(cameraUpdate)
            }
        }
    }

    private fun permissionGrantedMapUiSetting(){
        with(naverMap){

            // 내장 위치 추적 기능 사용
            locationSource = mLocationSource
            //minZoom = 5.0
            //maxZoom = 18.0

            naverMap.addOnOptionChangeListener {
                val mode = naverMap.locationTrackingMode.name
                val currentLocation = mLocationSource.lastLocation
                when(mode){
                    "None" -> locationTrackingMode = LocationTrackingMode.Follow
                    "Follow", "NoFollow" ->{
                        // 현재 위치 버튼을 눌렀을 때 카메라가 줌이 너무 작아지는걸 방지
                        if (currentLocation != null){
                            val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                            val cameraPosition = CameraPosition(latLng, 14.0)

                            naverMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition).animate(CameraAnimation.Easing))
                        }
                    }
                }
            }

            /**
             * 사용자의 위치를 지도에서 추적하는 모드
             *
             * Follow : 위치를 추적하면서 카메라도 따라 움직이는 모드.
             * LocationOverlay와 카메라의 좌표가 사용자의 위치를 따라 움직입니다.
             * API나 제스처를 사용해 지도를 임의로 움직일 경우 모드가 NoFollow로 바뀝니다.
             *
             * NoFollow : 위치는 추적하지만 지도는 움직이지 않는 모드.
             * LocationOverlay가 사용자의 위치를 따라 움직이나 지도는 움직이지 않습니다.
             *
             * Face : 위치를 추적하면서 카메라의 좌표와 베어링도 따라 움직이는 모드.
             * LocationOverlay와 카메라의 좌표,
             * 베어링이 사용자의 위치, 사용자가 바라보고 있는 방향을 따라 움직입니다.
             * API나 제스처를 사용해 지도를 임의로 움직일 경우 모드가 NoFollow로 바뀝니다.
             *
             * */
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

    private fun permissionDeniedMapUiSetting(){
        with(naverMap){

            // 건물 내부 표시
            isIndoorEnabled = true

            with(uiSettings){
                // 줌버튼
                isZoomControlEnabled = false

                // 실내지도 층 피커
                isIndoorLevelPickerEnabled = true

                // 축적바
                isScaleBarEnabled = true
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
            val marker = Marker()
            with(marker){
                icon = OverlayImage.fromResource(R.drawable.ic_marker)
                position = LatLng(
                    store.storeInfo.latitude.toDouble(),
                    store.storeInfo.longitude.toDouble()
                )
                map = naverMap
                width = 82
                height = 86
            }
        }
    }
}