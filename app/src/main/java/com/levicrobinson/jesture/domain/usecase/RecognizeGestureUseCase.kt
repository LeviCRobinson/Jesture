package com.levicrobinson.jesture.domain.usecase

import com.levicrobinson.jesture.domain.model.AccelerometerReading
import com.levicrobinson.jesture.domain.model.Gesture
import com.levicrobinson.jesture.domain.repository.GestureRepository
import jakarta.inject.Inject

class RecognizeGestureUseCase @Inject constructor(
    private val gestureRepository: GestureRepository
) {
    suspend operator fun invoke(accelerometerReadings: List<AccelerometerReading>): Gesture? {

        return gestureRepository.recognizeGesture(accelerometerReadings)
    }
}