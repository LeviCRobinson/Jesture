package com.levicrobinson.jesture.data.model

import com.google.gson.annotations.SerializedName

data class AllGesturesResponse(
    val allGestures: List<NetworkGesture>
)

data class GestureCreateResponse(
    @SerializedName("createGesture")
    val createGestureWithFrames: NetworkGesture
)

data class DeleteGestureResponse(
    val deleteGesture: String
)

data class RecognizeGestureResponse(
    @SerializedName("getMatchingGesture")
    val matchingGesture: NetworkGesture
)