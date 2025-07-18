package com.levicrobinson.jesture.ui.utils

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission

object HapticsUtils {
    private fun buildVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(VibratorManager::class.java)
            vibratorManager?.defaultVibrator
        } else {
            context.getSystemService(Vibrator::class.java)
        }
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    fun normalClick(context: Context) {
        val vibrator = buildVibrator(context)
        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    fun heavyClick(context: Context) {
        val vibrator = buildVibrator(context)
        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
    }
}