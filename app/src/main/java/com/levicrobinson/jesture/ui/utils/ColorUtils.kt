package com.levicrobinson.jesture.ui.utils

import androidx.compose.ui.graphics.Color

val Color.disabled: Color
    get() = copy(alpha=0.5f)