package com.levicrobinson.jesture.domain.usecase

import com.levicrobinson.jesture.domain.repository.SensorRepository

class StopGestureRecordUseCase (
    private val sensorRepository: SensorRepository
) {
    operator fun invoke() = sensorRepository.stopGestureRecord()
}