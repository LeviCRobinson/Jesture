package com.levicrobinson.jesture.ui.common.composables

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.levicrobinson.jesture.R
import com.levicrobinson.jesture.ui.theme.CancelGray
import com.levicrobinson.jesture.ui.theme.ConfirmGreen

@Composable
fun ConfirmIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        colors = IconButtonDefaults.filledIconButtonColors()
            .copy(containerColor = ConfirmGreen, contentColor = Color.White),
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_semirounded)),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = stringResource(R.string.confirm_button_text)
        )
    }
}

@Composable
fun DismissIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        colors = IconButtonDefaults.filledIconButtonColors()
            .copy(containerColor = CancelGray, contentColor = Color.White),
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_semirounded)),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.dismiss_button_text)
        )
    }
}