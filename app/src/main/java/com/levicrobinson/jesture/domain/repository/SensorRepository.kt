package com.levicrobinson.jesture.domain.repository

interface SensorRepository {
    fun startGestureRecord()
    fun stopGestureRecord()
}