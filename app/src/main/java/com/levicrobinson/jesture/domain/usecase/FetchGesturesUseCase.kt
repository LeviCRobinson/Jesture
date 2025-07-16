package com.levicrobinson.jesture.domain.usecase

import com.levicrobinson.jesture.domain.model.Gesture
import com.levicrobinson.jesture.domain.repository.GestureRepository
import jakarta.inject.Inject

class FetchGesturesUseCase @Inject constructor(
    private val gestureRepository: GestureRepository
) {
    suspend operator fun invoke(): List<Gesture>? = gestureRepository.fetchGestures()
}