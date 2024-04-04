package com.myproject.cloudbridge.ext

import android.view.View
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

// 클릭 이벤트를 flow로 변환
fun View.clicks(): Flow<Unit> = callbackFlow {
    setOnClickListener {
        this.trySend(Unit)
    }
    awaitClose { setOnClickListener(null) }
}
fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = flow{
    var lastEmissionTime = 0L
    collect{ upstream ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmissionTime > windowDuration) {
            lastEmissionTime = currentTime
            emit(upstream)
        }
    }
}