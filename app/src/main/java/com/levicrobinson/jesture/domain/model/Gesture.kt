package com.levicrobinson.jesture.domain.model

data class Gesture(
    val id: Int,
    val name: String,
    val frames: List<Frame>
)

data class Frame(
    val accelX: Float,
    val accelY: Float,
    val accelZ: Float
)