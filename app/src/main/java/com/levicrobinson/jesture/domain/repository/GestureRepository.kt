package com.levicrobinson.jesture.domain.repository

import com.levicrobinson.jesture.domain.model.Gesture

interface GestureRepository {
    suspend fun fetchGestures(): List<Gesture>
}