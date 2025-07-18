package com.levicrobinson.jesture.domain.model

data class Gesture(
    val id: Int,
    val name: String,
    val description: String,
    val accelerometerReadings: List<AccelerometerReading>
)

fun Gesture.toGraphQLVariables() = mapOf(
    "gestureInput" to mapOf(
        "name" to this.name.trim(),
        "description" to this.description.trim(),
        "frames" to this.accelerometerReadings.map {
            mapOf(
                "accelX" to it.accelX,
                "accelY" to it.accelY,
                "accelZ" to it.accelZ
            )
        }
    )
)