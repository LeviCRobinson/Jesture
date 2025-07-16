package com.levicrobinson.jesture.data.api

import com.levicrobinson.jesture.BuildConfig
import com.levicrobinson.jesture.data.model.AllGesturesResponse
import com.levicrobinson.jesture.data.model.DeleteGestureResponse
import com.levicrobinson.jesture.data.model.GestureCreateResponse
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

    @Headers("x-api-key: ${BuildConfig.GESTURE_API_KEY}")
    @POST("/graphql")
    suspend fun createGesture(
        @Body body: GraphQLRequest
    ): Response<GraphQLResponse<GestureCreateResponse>>

    @Headers("x-api-key: ${BuildConfig.GESTURE_API_KEY}")
    @POST("/graphql")
    suspend fun deleteGesture(
        @Body body: GraphQLRequest
    ): Response<GraphQLResponse<DeleteGestureResponse>>
}