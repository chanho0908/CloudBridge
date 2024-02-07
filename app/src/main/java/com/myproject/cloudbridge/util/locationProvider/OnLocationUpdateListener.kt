package com.myproject.cloudbridge.util.locationProvider

import android.location.Location

// 위치 업데이트가 발생할 때 리스너를 통해 위치 정보를 전달하는 인터페이스
interface OnLocationUpdateListener {
    fun onLocationUpdated(location: Location)
}