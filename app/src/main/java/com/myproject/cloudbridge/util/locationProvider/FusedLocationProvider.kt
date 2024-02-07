package com.myproject.cloudbridge.util.locationProvider
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class FusedLocationProvider (
    private val context: Context,
    private val listener: OnLocationUpdateListener
) {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    init {
        initLocationClient()
        initLocationCallback()
    }

    // 1. 위치 서비스를 관리하는 클라이언트를 초기화
    private fun initLocationClient() {
        Log.d("FusedLocationManager", "initLocationClient() start ")
        // 위치 서비스를 관리하고 제공하는 클라이언트
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        // priority : 위치 정확도
        // BALANCED_POWER_ACCURACY : 약 100m 정도의 대략적인 도시 블록 내의 위치 정밀도, 전력을 비교적 적게 사용
        // HIGH_ACCURACY : 가장 적확한 위치 요청, GPS를 사용해 위치를 확인할 가능성이 높음
        // LOW_POWER : 약 10km 정도의 도시 수준의 정밀도, 아주 대략적인 수준으로 전력을 더 적게 소비
        // NO_POWER : 전력 소비에 영향을 거의 미치지 않으며 사용, 앱에서 위치를 트리거 하지 않고 다른 앱에서 트리거 한 정보 사용
        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // 위치 업데이트 요청을 위한 요청
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        // 위치 서비스를 사용하기 전에 사용자의 위치 설정을 확인하거나 변경하기 위해 사용
        // 위치 서비스를 사용할 때 GPS나 네트워크 기반의 위치 정보를 활성화 하도록 사용자에게 요청
        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            Log.d(TAG, "location client setting success")
        }

        task.addOnFailureListener {
            Log.d(TAG, "location client setting failure")
        }
    }

    // 2. 위치 업데이트를 처리하는 콜백 초기화
    private fun initLocationCallback() {
        Log.d(TAG, "initLocationCallback() start")
        // Fused Location Provider에서 위치 업데이트 이벤트를 수신하는 콜백 클래스
        // 위치 서비스로부터 새로운 위치 업데이트가 있을 때마다 호출
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    listener.onLocationUpdated(location)
                    break
                }
            }
        }
    }

    // 3. 위치 업데이트를 요청
    fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    // 4. 마지막으로 알려진 위치 정보를 요청
    fun requestLastLocation() {
        Log.d(TAG, "requestLastLocation() start")
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "requestLastLocation() : PERMISSION NOT GRANTED")
            return
        }
        Log.d(TAG, "requestLastLocation() 권한 허용")
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                Log.d(TAG, "requestLastLocation addOnSuccessListener start")
                Log.d(TAG, "requestLastLocation() : ${location.latitude} / ${location.longitude}")
                listener.onLocationUpdated(location)
            }
    }

    fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        private const val TAG = "FusedLocationManager"
    }
}