package com.levicrobinson.jesture.ui.home

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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.levicrobinson.jesture.R
import com.levicrobinson.jesture.domain.model.AccelerometerReading
import com.levicrobinson.jesture.domain.model.Gesture
import com.levicrobinson.jesture.ui.common.composables.EmptyStateView
import com.levicrobinson.jesture.ui.common.composables.LoadingStateView
import com.levicrobinson.jesture.ui.utils.HapticsUtils
import com.levicrobinson.jesture.ui.utils.disabled

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
            deleteGesture = viewModel::deleteGesture,
            submitGestureCreation = viewModel::submitGestureCreation,
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
    stopGestureRecord: () -> ArrayList<AccelerometerReading>?,
    submitGestureCreation: (String, String, List<AccelerometerReading>) -> Unit,
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
            onDismiss = { updateDialogType(HomeViewDialogType.NONE) },
            onConfirm = submitGestureCreation,
            startGestureRecord = startGestureRecord,
            stopGestureRecord = stopGestureRecord
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
                        gesture = gesture,
                        onDeleteClick = { updateDialogType(HomeViewDialogType.DELETE_GESTURE) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                    )
                    if (uiState.homeViewDialogType == HomeViewDialogType.DELETE_GESTURE) {
                        DeleteGestureDialog(
                            gesture = gesture,
                            onDismiss = { updateDialogType(HomeViewDialogType.NONE) },
                            onConfirm = { deleteGesture(gesture) }
                        )
                    }
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
    gesture: Gesture,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
                modifier = Modifier.weight(0.4f)
            )
            Spacer(modifier = Modifier.weight(0.6f))
            IconButton(
                onClick = onDeleteClick
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_gesture)
                )
            }
        }
    }

}

@Composable
private fun CreateGestureDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, List<AccelerometerReading>) -> Unit,
    startGestureRecord: () -> Unit,
    stopGestureRecord: () -> ArrayList<AccelerometerReading>?,
    modifier: Modifier = Modifier
) {
    var gestureNameEditValue by remember { mutableStateOf("") }
    var gestureDescriptionEditValue by remember { mutableStateOf("") }
    val inputsFilled = gestureNameEditValue.isNotBlank() && gestureDescriptionEditValue.isNotBlank()
    var isRecording by remember {mutableStateOf(false)}
    var gestureReadings by remember { mutableStateOf(listOf<AccelerometerReading>()) }
    val context = LocalContext.current
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
                IconButton (
                    onClick = onDismiss,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }

            TextField(
                value = gestureNameEditValue,
                onValueChange = { gestureNameEditValue = it },
                placeholder = { Text("Gesture Name") }
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))
            TextField(
                value = gestureDescriptionEditValue,
                onValueChange = { gestureDescriptionEditValue = it },
                placeholder = { Text("Gesture Description") }
            )

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = dimensionResource(R.dimen.padding_small))
            ) {
                TextButton(
                    onClick = {
                        onConfirm(gestureNameEditValue, gestureDescriptionEditValue, gestureReadings)
                        gestureReadings = listOf()
                    },
                    enabled = gestureNameEditValue.isNotBlank() && gestureDescriptionEditValue.isNotBlank() && gestureReadings.isNotEmpty()
                ) {
                    Text("Create")
                }
                Spacer(Modifier.width(dimensionResource(R.dimen.padding_large)))
                Box (
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.record_button_size))
                        .border(
                            width = dimensionResource(R.dimen.button_border_width),
                            shape = CircleShape,
                            color = if(inputsFilled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.disabled
                        )
                        .clip(CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    if(inputsFilled) {
                                        isRecording = true
                                        HapticsUtils.heavyClick(context)
                                        startGestureRecord()
                                    }
                                },
                                onPress = {
                                    if(inputsFilled) {
                                        tryAwaitRelease()
                                        HapticsUtils.normalClick(context)
                                        isRecording = false
                                        gestureReadings = stopGestureRecord() ?: arrayListOf()
                                    }
                                }
                            )
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.FiberManualRecord,
                        contentDescription = stringResource(R.string.record_gesture),
                        modifier = Modifier.size(dimensionResource(R.dimen.button_icon_size)),
                        tint = if(inputsFilled) Color.Red else Color.Red.disabled
                    )
                }
            }
        }
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
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    }
                ) {
                    Text(stringResource(R.string.confirm_button_text))
                }
                Spacer(Modifier.width(dimensionResource(R.dimen.padding_large)))
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(stringResource(R.string.dismiss_button_text))
                }
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