package com.levicrobinson.jesture.data.sensors

import dagger.assisted.AssistedFactory

@AssistedFactory
interface AccelerometerListenerFactory {
    fun create(onReading: (x: Float, y: Float, z: Float) -> Unit): AccelerometerListener
}