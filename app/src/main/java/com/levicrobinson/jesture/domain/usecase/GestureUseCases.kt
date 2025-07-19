package com.levicrobinson.jesture.domain.usecase

import javax.inject.Inject

data class GestureUseCases @Inject constructor(
    val fetchGestures: FetchGesturesUseCase,
    val createGesture: CreateGestureUseCase,
    val deleteGesture: DeleteGestureUseCase,
    val recognizeGestureUseCase: RecognizeGestureUseCase
)
