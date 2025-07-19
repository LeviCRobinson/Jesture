package com.levicrobinson.jesture.domain.repository

import com.levicrobinson.jesture.domain.model.AccelerometerReading
import com.levicrobinson.jesture.domain.model.Gesture

interface GestureRepository {
    suspend fun fetchGestures(): List<Gesture>?
    suspend fun createGesture(gesture: Gesture): Gesture?
    suspend fun deleteGesture(gesture: Gesture): String?
    suspend fun recognizeGesture(readings: List<AccelerometerReading>): Gesture?
}