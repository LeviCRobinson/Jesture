package com.levicrobinson.jesture.data.model

data class AllGesturesResponse(
    val allGestures: List<NetworkGesture>
)

data class GestureCreateResponse(
    val createGestureWithFrames: NetworkGesture
)