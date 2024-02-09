package com.myproject.cloudbridge.repository

import android.util.Log
import com.myproject.cloudbridge.util.Constants.Companion.SECRETE_KEY
import com.myproject.cloudbridge.model.store.CrnStateRequestModel
import com.myproject.cloudbridge.model.store.MyStoreInfoRequestModel
import com.myproject.cloudbridge.retrofit.CRNRetrofitInstance
import com.myproject.cloudbridge.retrofit.MySQLIStoreInstance

class NetworkRepository {
    private val storeInfoApiInstance = MySQLIStoreInstance.getStoreApiInstance()
    private val storeMenuApiInstance = MySQLIStoreInstance.getStoreMenuApiInstance()
    private val crnAPiInstance = CRNRetrofitInstance.getInstance()

    // 매장 정보 등록
    suspend fun registrationStore(store: MyStoreInfoRequestModel){
        storeInfoApiInstance.storeRegistration(
                store.storeImage, // 매장 이미지
                store.storeName,  // 매장명
                store.ceoName,    // 점주명
                store.crn,        // 사업자등록번호
                store.contact,    // 전화번호
                store.address,    // 주소
                store.latitude,   // 위도
                store.longitude,  // 경도
                store.kind)       // 업종
    }

    // 모든 매장의 정보 가져오기
    suspend fun getAllStoreInfo() = storeInfoApiInstance.getAllStoreInfo()

    // 나의 매장 정보 가져 오기
    suspend fun getMyStoreInfo(crn: String) = storeInfoApiInstance.getMyStoreInfo(crn)

    // 등록한 사업자 등록 번호 모두 가져오기
    suspend fun getCompanyRegistrationNumber() = storeInfoApiInstance.getCompanyRegistrationNumber()

    // 매장 업데이트
    suspend fun updateStoreInfo(store: MyStoreInfoRequestModel){
        Log.d("dasdsds", "NetworkRepository")
        storeInfoApiInstance.updateStoreInfo(
            store.storeImage,
            store.storeName,
            store.ceoName,
            store.crn,
            store.contact,
            store.address,
            store.latitude,
            store.longitude,
            store.kind
        )
    }


    // 매장 삭제
    suspend fun deleteMyStoreInfo(crn: String) = storeInfoApiInstance.deleteMyStoreInfo(crn)

    // 사업자 등록번호 API 호출
    suspend fun getCRNState(bno: CrnStateRequestModel) = crnAPiInstance.getCRNState(bno, SECRETE_KEY)


}