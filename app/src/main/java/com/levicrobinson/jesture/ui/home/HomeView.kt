package com.levicrobinson.jesture.ui.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.levicrobinson.jesture.R
import com.levicrobinson.jesture.domain.model.Gesture
import com.levicrobinson.jesture.ui.common.composables.ConfirmIconButton
import com.levicrobinson.jesture.ui.common.composables.DismissIconButton
import com.levicrobinson.jesture.ui.common.composables.EmptyStateView
import com.levicrobinson.jesture.ui.common.composables.LoadingStateView
import com.levicrobinson.jesture.ui.utils.HapticsUtils
import com.levicrobinson.jesture.ui.utils.disabled
import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    when (uiState) {
        is HomeViewUiState.Success -> SuccessView(
            uiState = uiState,
            startGestureRecord = viewModel::startGestureRecord,
            stopGestureRecord = viewModel::stopGestureRecord,
            submitGestureCreation = viewModel::submitGestureCreation,
            submitGestureRecognition = viewModel::submitGestureRecognition,
            updateDialogGestureName = viewModel::updateDialogGestureName,
            updateDialogGestureDescription = viewModel::updateDialogGestureDescription,
            canConfirmGestureCreation = viewModel::canConfirmGestureCreation,
            deleteGesture = viewModel::deleteGesture,
            updateDialogType = viewModel::updateDialogType
        )

        HomeViewUiState.Error -> ErrorView(modifier = modifier.fillMaxSize())

        HomeViewUiState.Loading -> LoadingView(modifier = modifier.fillMaxSize())
    }
}

@Composable
private fun SuccessView(
    uiState: HomeViewUiState.Success,
    startGestureRecord: () -> Unit,
    stopGestureRecord: () -> Unit,
    submitGestureCreation: () -> Unit,
    submitGestureRecognition: (Context) -> Unit,
    updateDialogGestureName: (String) -> Unit,
    updateDialogGestureDescription: (String) -> Unit,
    canConfirmGestureCreation: () -> Boolean,
    deleteGesture: (Gesture) -> Unit,
    updateDialogType: (HomeViewDialogType) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column {
            GestureList(
                uiState = uiState,
                deleteGesture = deleteGesture,
                updateDialogType = updateDialogType,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            Column {
                val context = LocalContext.current
                GestureRecordButton(
                    startGestureRecord = startGestureRecord,
                    stopGestureRecord = {
                        stopGestureRecord()
                        submitGestureRecognition(context)
                    },
                    enabled = true,
                    modifier = Modifier.size(dimensionResource(R.dimen.large_record_button_size))
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            Column {
                FloatingActionButton(
                    onClick = { updateDialogType(HomeViewDialogType.CREATE_GESTURE) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_gesture)
                    )
                }
                Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))
            }
            Spacer(Modifier.width(dimensionResource(R.dimen.padding_large)))
        }
    }

    if (uiState.homeViewDialogType == HomeViewDialogType.CREATE_GESTURE) {
        CreateGestureDialog(
            uiState = uiState,
            onDismiss = { updateDialogType(HomeViewDialogType.NONE) },
            onConfirm = submitGestureCreation,
            startGestureRecord = startGestureRecord,
            stopGestureRecord = stopGestureRecord,
            updateDialogGestureName = updateDialogGestureName,
            updateDialogGestureDescription = updateDialogGestureDescription,
            canConfirmGestureCreation = canConfirmGestureCreation
        )
    }
}

@Composable
fun GestureList(
    uiState: HomeViewUiState.Success,
    deleteGesture: (Gesture) -> Unit,
    updateDialogType: (HomeViewDialogType) -> Unit,
    modifier: Modifier = Modifier
) {
    uiState.gestures?.let {
        if (it.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.padding_medium))
            ) {
                itemsIndexed(
                    items = uiState.gestures,
                    key = { index, gesture -> gesture.id }
                ) { index, gesture ->
                    GestureCard(
                        uiState = uiState,
                        gesture = gesture,
                        onDeleteClick = { updateDialogType(HomeViewDialogType.DELETE_GESTURE) },
                        updateDialogType = updateDialogType,
                        deleteGesture = deleteGesture,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                    )
                }
            }

        } else {
            EmptyGesturesView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.padding_medium))
            )
        }
    }

}

@Composable
private fun GestureCard(
    uiState: HomeViewUiState.Success,
    gesture: Gesture,
    onDeleteClick: () -> Unit,
    updateDialogType: (HomeViewDialogType) -> Unit,
    deleteGesture: (Gesture) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var gestureIdToDelete by remember { mutableStateOf(0) }
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_rounded)))
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    Toast.makeText(context, gesture.description, Toast.LENGTH_SHORT).show()
                }
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_medium),
                    vertical = dimensionResource(R.dimen.padding_small)
                )
                .height(dimensionResource(R.dimen.normal_card_height))
        ) {
            Text(
                gesture.name,
                modifier = Modifier.weight(0.8f),
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(0.2f))
            IconButton(
                onClick = {
                    gestureIdToDelete = gesture.id
                    onDeleteClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_gesture)
                )
            }
        }
    }

    if (uiState.homeViewDialogType == HomeViewDialogType.DELETE_GESTURE && gesture.id == gestureIdToDelete) {
        DeleteGestureDialog(
            gesture = gesture,
            onDismiss = {
                updateDialogType(HomeViewDialogType.NONE)
                gestureIdToDelete = 0
            },
            onConfirm = { deleteGesture(gesture) }
        )
    }

}

@Composable
private fun CreateGestureDialog(
    uiState: HomeViewUiState.Success,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    startGestureRecord: () -> Unit,
    stopGestureRecord: () -> Unit,
    updateDialogGestureName: (String) -> Unit,
    updateDialogGestureDescription: (String) -> Unit,
    canConfirmGestureCreation: () -> Boolean,
    modifier: Modifier = Modifier
) {
    val textInputsFilled =
        uiState.gestureDialogInputs.gestureName.isNotBlank() && uiState.gestureDialogInputs.gestureDescription.isNotBlank()
    var isRecording by remember { mutableStateOf(false) }
    var useTimeTickHeavyClick by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(isRecording) {
        while (isRecording) {
            if (useTimeTickHeavyClick) {
                HapticsUtils.heavyClick(context)
            } else {
                HapticsUtils.normalClick(context)
            }
            delay(500)
            useTimeTickHeavyClick = !useTimeTickHeavyClick
        }
        useTimeTickHeavyClick = false
    }
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = modifier
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(dimensionResource(R.dimen.corner_rounded))
                )
                .padding(all = dimensionResource(R.dimen.padding_medium))
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(vertical = dimensionResource(R.dimen.padding_small))
            ) {
                IconButton(
                    onClick = onDismiss,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }

            TextField(
                value = uiState.gestureDialogInputs.gestureName,
                onValueChange = { updateDialogGestureName(it) },
                placeholder = { Text(stringResource(R.string.gesture_name_field_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))
            TextField(
                value = uiState.gestureDialogInputs.gestureDescription,
                onValueChange = { updateDialogGestureDescription(it) },
                placeholder = { Text(stringResource(R.string.gesture_description_field_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = dimensionResource(R.dimen.padding_small))
            ) {
                ConfirmIconButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    enabled = canConfirmGestureCreation()
                )
                Spacer(Modifier.width(dimensionResource(R.dimen.padding_large)))
                GestureRecordButton(
                    startGestureRecord = startGestureRecord,
                    stopGestureRecord = stopGestureRecord,
                    enabled = textInputsFilled
                )
            }
        }
    }
}

/**
 * A button that denotes the beginning and end of an accelerometer recording.  When pressed, gesture
 * record will begin, and when the button is released, gesture record will stop.  This will also trigger
 * timing haptics to give the user a sense of how long the gesture is.
 * @param startGestureRecord - Function called to begin gesture recording
 * @param stopGestureRecord - Function called to stop gesture recording
 * @param enabled - Whether the button is enabled or not; also changes styling.
 */
@Composable
private fun GestureRecordButton(
    startGestureRecord: () -> Unit,
    stopGestureRecord: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var isRecording by remember { mutableStateOf(false) }
    var useTimeTickHeavyClick by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(isRecording) {
        while (isRecording) {
            if (useTimeTickHeavyClick) {
                HapticsUtils.heavyClick(context)
            } else {
                HapticsUtils.normalClick(context)
            }
            delay(500)
            useTimeTickHeavyClick = !useTimeTickHeavyClick
        }
        useTimeTickHeavyClick = false
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(dimensionResource(R.dimen.record_button_size))
            .border(
                width = dimensionResource(R.dimen.button_border_width),
                shape = CircleShape,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.disabled
            )
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        // Begins gesture record on press
                        if (enabled) {
                            startGestureRecord()
                            isRecording = true

                            // When released, gesture record ends.
                            val released = try {
                                tryAwaitRelease()
                            } catch (_: CancellationException) {
                                false
                            }
                            if (released) {
                                HapticsUtils.normalClick(context)
                                stopGestureRecord()
                                isRecording = false
                            }
                        }
                    }
                )
            }
    ) {
        Icon(
            imageVector = Icons.Default.FiberManualRecord,
            contentDescription = stringResource(R.string.record_gesture),
            modifier = Modifier.size(dimensionResource(R.dimen.button_icon_size)),
            tint = if (enabled) Color.Red else Color.Red.disabled
        )
    }
}

@Composable
private fun DeleteGestureDialog(
    gesture: Gesture,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = modifier
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(dimensionResource(R.dimen.corner_rounded))
                )
                .padding(all = dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                text = stringResource(
                    R.string.delete_gesture_confirmation_description,
                    gesture.name
                )
            )
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                DismissIconButton(onClick = onDismiss)
                Spacer(Modifier.width(dimensionResource(R.dimen.padding_large)))
                ConfirmIconButton(onClick = onConfirm)
            }
        }
    }
}

@Composable
private fun ErrorView(modifier: Modifier = Modifier) {
    Text("Error", modifier = modifier)
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    LoadingStateView(
        descriptionTextRes = R.string.loading_gesture_list_description,
        modifier = modifier
    )
}

@Composable
private fun EmptyGesturesView(modifier: Modifier = Modifier) {
    EmptyStateView(
        descriptionTextRes = R.string.empty_gesture_list_description,
        iconImageVector = Icons.Default.Search,
        iconContentDescriptionRes = R.string.no_gestures_found_content_description,
        modifier = modifier
    )
}