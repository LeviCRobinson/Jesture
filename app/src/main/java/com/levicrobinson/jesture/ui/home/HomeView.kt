package com.levicrobinson.jesture.ui.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.levicrobinson.jesture.R
import com.levicrobinson.jesture.domain.model.Frame
import com.levicrobinson.jesture.domain.model.Gesture
import com.levicrobinson.jesture.ui.common.composables.EmptyStateView
import com.levicrobinson.jesture.ui.common.composables.LoadingStateView
import kotlin.random.Random

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    when (uiState) {
        is HomeViewUiState.Success -> SuccessView(
            uiState = uiState,
            deleteGesture = viewModel::deleteGesture,
            submitGestureCreation = viewModel::submitGestureCreation
        )

        HomeViewUiState.Error -> ErrorView(modifier = modifier.fillMaxSize())

        HomeViewUiState.Loading -> LoadingView(modifier = modifier.fillMaxSize())
    }
}

@Composable
private fun SuccessView(
    uiState: HomeViewUiState.Success,
    submitGestureCreation: (Gesture) -> Unit,
    deleteGesture: (Gesture) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddGestureDialog by remember { mutableStateOf(false) }
    Box (modifier = modifier) {
        GestureList(
            uiState = uiState,
            deleteGesture = deleteGesture
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            Column {
                FloatingActionButton(
                    onClick = { showAddGestureDialog = !showAddGestureDialog }
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
    if (showAddGestureDialog) {
        CreateGestureDialog(
            onDismiss = {showAddGestureDialog = false},
            onConfirm = submitGestureCreation
        )
    }
}

@Composable
fun GestureList(
    uiState: HomeViewUiState.Success,
    deleteGesture: (Gesture) -> Unit,
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
    gesture: Gesture,
    deleteGesture: (Gesture) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_rounded)))
            .combinedClickable(
                onClick = {},
                onLongClick = { Toast.makeText(context, gesture.description, Toast.LENGTH_SHORT).show() }
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
                onClick = { deleteGesture(gesture) }
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
    onConfirm: (Gesture) -> Unit,
    modifier: Modifier = Modifier
) {
    var gestureNameEditValue by remember { mutableStateOf("") }
    var gestureDescriptionEditValue by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column (
            modifier = modifier
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(dimensionResource(R.dimen.corner_rounded))
                )
                .padding(all = dimensionResource(R.dimen.padding_medium))
        ){
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
            Row (
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ){
                TextButton(
                    onClick = {
                        val frames = listOf(
                            Frame(
                                Random.nextFloat().coerceIn(0.0f, 1.0f),
                                Random.nextFloat().coerceIn(0.0f, 1.0f),
                                Random.nextFloat().coerceIn(0.0f, 1.0f)
                            ),
                            Frame(
                                Random.nextFloat().coerceIn(0.0f, 1.0f),
                                Random.nextFloat().coerceIn(0.0f, 1.0f),
                                Random.nextFloat().coerceIn(0.0f, 1.0f)
                            ),
                            Frame(
                                Random.nextFloat().coerceIn(0.0f, 1.0f),
                                Random.nextFloat().coerceIn(0.0f, 1.0f),
                                Random.nextFloat().coerceIn(0.0f, 1.0f)
                            )
                        )
                        onConfirm(Gesture(id=0, name=gestureNameEditValue, description = gestureDescriptionEditValue, frames = frames))
                        onDismiss()
                    },
                    enabled = gestureNameEditValue.trim().isNotEmpty() && gestureDescriptionEditValue.trim().isNotEmpty()
                ) {
                    Text("Confirm")
                }
                Spacer(Modifier.width(dimensionResource(R.dimen.padding_large)))
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Dismiss")
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