package com.levicrobinson.jesture.data.api

import com.levicrobinson.jesture.BuildConfig
import com.levicrobinson.jesture.data.model.AllGesturesResponse
import com.levicrobinson.jesture.data.model.GraphQLRequest
import com.levicrobinson.jesture.data.model.GraphQLResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GestureApi {
    @Headers("x-api-key: ${BuildConfig.GESTURE_API_KEY}")
    @POST("/graphql")
    suspend fun getGestures(
        @Body body: GraphQLRequest
    ): Response<GraphQLResponse<AllGesturesResponse>>
}