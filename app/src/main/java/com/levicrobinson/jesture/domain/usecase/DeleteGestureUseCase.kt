package com.levicrobinson.jesture.domain.usecase

import com.levicrobinson.jesture.domain.model.Gesture
import com.levicrobinson.jesture.domain.repository.GestureRepository
import jakarta.inject.Inject

class DeleteGestureUseCase @Inject constructor(
    private val gestureRepository: GestureRepository
) {
    suspend operator fun invoke(gesture: Gesture): String? = gestureRepository.deleteGesture(gesture)
}