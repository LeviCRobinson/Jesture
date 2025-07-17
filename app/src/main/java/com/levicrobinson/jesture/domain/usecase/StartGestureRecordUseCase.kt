package com.levicrobinson.jesture.domain.usecase

import com.levicrobinson.jesture.domain.repository.SensorRepository

class StartGestureRecordUseCase (
    private val sensorRepository: SensorRepository
) {
    operator fun invoke() = sensorRepository.startGestureRecord()
}