package com.levicrobinson.jesture.data.api

import com.levicrobinson.jesture.BuildConfig
import com.levicrobinson.jesture.domain.model.Gesture
import retrofit2.http.GET
import retrofit2.http.Headers

interface GestureApi {
    @Headers("x-api-key: ${BuildConfig.GESTURE_API_KEY}")
    @GET("/gestures/list")
    suspend fun getGestures(): List<Gesture>
}