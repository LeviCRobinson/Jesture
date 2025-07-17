package com.levicrobinson.jesture.data.repository

import android.content.Context
import com.levicrobinson.jesture.data.sensors.AccelerometerListener
import com.levicrobinson.jesture.domain.repository.SensorRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext

class SensorRepositoryImpl @AssistedInject constructor(
    @ApplicationContext context: Context,
    @Assisted private val onReading: (Float, Float, Float) -> Unit
): SensorRepository {
    private val accelerometerListener = AccelerometerListener(context, onReading)
    override fun startGestureRecord() {
        accelerometerListener.start()
    }

    override fun stopGestureRecord() {
        accelerometerListener.stop()
    }

    @AssistedFactory
    interface Factory {
        fun create(onReading: (Float, Float, Float) -> Unit): SensorRepositoryImpl
    }
}