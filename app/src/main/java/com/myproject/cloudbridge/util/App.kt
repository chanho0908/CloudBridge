package com.myproject.cloudbridge.util

import android.app.Application
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.kakao.sdk.common.KakaoSdk
import com.myproject.cloudbridge.util.Utils.APP_KEY

class App: Application() {
    init {
        instance = this
    }

    companion object{
        lateinit var db: FirebaseFirestore
        lateinit var storage: FirebaseStorage

        private var instance: App? = null

        fun context(): Context? {
            return instance?.applicationContext
        }
    }


    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, APP_KEY)
        db = FirebaseFirestore.getInstance()
        storage = Firebase.storage
    }


}