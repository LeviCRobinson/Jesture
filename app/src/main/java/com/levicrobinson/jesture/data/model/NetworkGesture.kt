package com.levicrobinson.jesture.data.model

import com.levicrobinson.jesture.domain.model.Frame
import com.levicrobinson.jesture.domain.model.Gesture



data class NetworkGesture(
    val id: Int,
    val name: String,
    val description: String,
    val frames: List<NetworkFrame>
)

data class NetworkFrame(
    val id: Int,
    val accelX: Float,
    val accelY: Float,
    val accelZ: Float
)

fun NetworkGesture.toDomainModel() = Gesture(id, name, description, frames.map{ it.toDomainModel() })
fun NetworkFrame.toDomainModel() = Frame(accelX, accelY, accelZ)