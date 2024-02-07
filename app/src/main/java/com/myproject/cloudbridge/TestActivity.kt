package com.myproject.cloudbridge

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.myproject.cloudbridge.util.locationProvider.FusedLocationProvider
import com.myproject.cloudbridge.util.locationProvider.OnLocationUpdateListener

class TestActivity: AppCompatActivity(), OnLocationUpdateListener {

    private lateinit var fusedLocationProvider: FusedLocationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        fusedLocationProvider = FusedLocationProvider(this, this)

        fusedLocationProvider.requestLastLocation()
    }

    override fun onStop() {
        super.onStop()
        fusedLocationProvider.stopLocationUpdates()
    }

    override fun onLocationUpdated(location: Location) {
        // 새 위치 업데이트가 있을 때 호출됩니다.
        Log.d(TAG, "New Location: ${location.latitude}, ${location.longitude}")
        // 위치 정보를 사용하는 작업을 수행합니다.
        //fusedLocationProvider.startLocationUpdates()
    }

    companion object {
        private const val TAG = "LocationActivity"
    }
}