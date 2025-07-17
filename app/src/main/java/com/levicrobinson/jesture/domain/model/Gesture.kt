package com.levicrobinson.jesture.domain.model

data class Gesture(
    val id: Int,
    val name: String,
    val description: String,
    val accelerometerReadings: List<AccelerometerReading>
)