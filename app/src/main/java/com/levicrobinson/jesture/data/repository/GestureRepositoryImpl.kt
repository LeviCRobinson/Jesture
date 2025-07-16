package com.levicrobinson.jesture.data.repository

import com.levicrobinson.jesture.data.api.GestureApi
import com.levicrobinson.jesture.domain.model.Frame
import com.levicrobinson.jesture.domain.model.Gesture
import com.levicrobinson.jesture.domain.repository.GestureRepository
import jakarta.inject.Inject

class GestureRepositoryImpl @Inject constructor(
    private val gestureApi: GestureApi
): GestureRepository {
    override suspend fun fetchGestures(): List<Gesture> {
        val gesture = Gesture(
            id = 1,
            name = "Gesture 1",
            frames = listOf(Frame(1f, 1f, 1f), Frame(2f, 2f, 2f))
        )
        return listOf(gesture)
    }
}