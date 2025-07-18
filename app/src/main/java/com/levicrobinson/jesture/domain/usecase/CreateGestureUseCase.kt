package com.levicrobinson.jesture.domain.usecase

import com.levicrobinson.jesture.domain.model.AccelerometerReading
import com.levicrobinson.jesture.domain.model.Gesture
import com.levicrobinson.jesture.domain.repository.GestureRepository
import jakarta.inject.Inject

class CreateGestureUseCase @Inject constructor(
    private val gestureRepository: GestureRepository
) {
    suspend operator fun invoke(gestureName: String, gestureDescription: String, accelerometerReadings: List<AccelerometerReading>): Gesture? {
        val gestureToCreate = Gesture(
            id=0,
            name = gestureName,
            description = gestureDescription,
            accelerometerReadings = accelerometerReadings
        )
        return gestureRepository.createGesture(gestureToCreate)
    }
}