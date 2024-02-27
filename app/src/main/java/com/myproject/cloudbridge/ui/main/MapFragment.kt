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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapView
import com.kakao.vectormap.animation.Interpolation
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelAnimator
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTransition
import com.kakao.vectormap.label.Transition
import com.kakao.vectormap.label.animation.DropAnimation
import com.kakao.vectormap.label.animation.ScaleAlphaAnimation
import com.kakao.vectormap.label.animation.ScaleAlphaAnimations
import com.myproject.cloudbridge.R
import com.myproject.cloudbridge.databinding.FragmentMapBinding
import com.myproject.cloudbridge.ui.search.SearchActivity
import com.myproject.cloudbridge.util.hasLocationPermission
import com.myproject.cloudbridge.util.locationProvider.FusedLocationProvider
import com.myproject.cloudbridge.util.locationProvider.OnLocationUpdateListener
import com.myproject.cloudbridge.util.showPermissionSnackBar
import com.myproject.cloudbridge.util.singleton.Utils.REQUEST_LOCATION_PERMISSIONS
import com.myproject.cloudbridge.viewModel.StoreManagementViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MapFragment : Fragment(), OnLocationUpdateListener, View.OnClickListener {
    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding get() = _binding!!
    private lateinit var launcherForPermission: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationProvider: FusedLocationProvider
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private lateinit var labelLayer: LabelLayer
    private lateinit var map: KakaoMap

    // MapCoordType.WGS84 을 나타내는 좌표 클래스
    // WGS84 : 경위도, GPS가 사용하는 좌표계
    private lateinit var currentLocation: LatLng
    private val viewModel: StoreManagementViewModel by viewModels()

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
        showMapWithUserCurrentLocation()
    }

    private fun initView(view: View) {
        launcherForPermission.launch(REQUEST_LOCATION_PERMISSIONS)

        sheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.store_bottom_sheet))
        with(sheetBehavior) {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        viewLifecycleOwner.lifecycleScope.launch {
            binding.progressBar.setProgressCompat(60, true)
            delay(420)
            binding.progressBar.setProgressCompat(100, true)
        }

        binding.searchView.setOnClickListener(this)
        binding.locationButton.setOnClickListener(this)
    }

    private fun initActivityProcess() {
        fusedLocationProvider = FusedLocationProvider(requireContext(), this)

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

    private fun showMapWithUserCurrentLocation() {
        with(binding) {
            // MapReadyCallback 을 통해 지도가 정상적으로 시작된 후에 수신할 수 있다.
            val KakaoMapReadyCallback = object : KakaoMapReadyCallback() {
                override fun onMapReady(kakaoMap: KakaoMap) {
                    map = kakaoMap
                    labelLayer = kakaoMap.labelManager?.layer!!

                    with(kakaoMap) {
                        // 나침반 설정
                        compass?.show()

                        // 위치 권한이 허용되지 않으면 기본 위치를 보여줌
                        if (!requireContext().hasLocationPermission()) {
                            val seoulLatitude = 37.5665
                            val seoulLongitude = 126.9780
                            moveCamera(
                                CameraUpdateFactory.newCenterPosition(
                                    LatLng.from(
                                        seoulLatitude,
                                        seoulLongitude
                                    )
                                )
                            )
                        } else {
                            // 카메라를 이동
                            map.moveCamera(CameraUpdateFactory.newCenterPosition(currentLocation))

                        }
                    }
                }
            }

            // MapView : 지도가 보여지는 뷰
            val view = MapView(requireContext())
            mapView.addView(view)
            mapView.start(KakaoMapReadyCallback)
            makeMaker()
        }
    }

    private fun makeMaker() {
        viewModel.fetchAllStoreFromRoom()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetch.collect{ fetched ->
                    if (fetched == true){
                        viewModel.allStoreData.collect {
                            Log.d("sdasdas", "collect")
                            labelLayer.removeAll()
                            it?.map { store ->

                                val pos = LatLng.from(store.storeInfo.latitude.toDouble(), store.storeInfo.longitude.toDouble())

                                showAnimationLabel(pos)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 마커 애니메이션 수정
     *
     * */
    private fun showAnimationLabel(pos: LatLng) {

        val label = labelLayer.addLabel(
            LabelOptions.from(pos)
                .setStyles(R.drawable.ic_marker_128)
        )

        // 애니메이션 설정
        val dropAnimation = DropAnimation.from("dropAnimator")
        dropAnimation.setRemoveLabelAtStop(true)
        dropAnimation.setPixelHeight(500f)
        dropAnimation.setInterpolation(Interpolation.Linear)
        // 반복 횟수
        dropAnimation.setRepeatCount(0)

        // 애니메이션 생성
        val animator: LabelAnimator = map.labelManager?.addAnimator(dropAnimation)!!

        // 애니메이터에 라벨 추가
        animator.addLabels(label)

        // 애니메이션 시작
        animator.start()
        map.moveCamera(
            CameraUpdateFactory.newCenterPosition(pos, 14),
            CameraAnimation.from(500)
        )
    }


    override fun onLocationUpdated(location: Location) {
        currentLocation = LatLng.from(location.latitude, location.longitude)
    }

    override fun onStop() {
        super.onStop()
        fusedLocationProvider.stopLocationUpdates()
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.search_view -> {
                startActivity(Intent(requireContext(), SearchActivity::class.java))
            }
            R.id.location_button -> {
                // 내 위치로 가기 버튼을 누르면
                // 위치 권한이 허용 됐다면
                if (requireContext().hasLocationPermission()) {
                    // 마지막 위치를 요청해서
                    fusedLocationProvider.requestLastLocation()

                    // 카메라를 이동
                    map.moveCamera(CameraUpdateFactory.newCenterPosition(currentLocation))
                } else {
                    // 위치 권한이 허용되지 않았다면 권한 요청을 보냄
                    requireContext().showPermissionSnackBar(binding.root)
                }

            }
        }
    }
}