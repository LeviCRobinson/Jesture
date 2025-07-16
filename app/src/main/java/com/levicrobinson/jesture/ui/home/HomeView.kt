package com.levicrobinson.jesture.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.levicrobinson.jesture.R

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    when (uiState) {
        is HomeViewUiState.Success -> SuccessView(
            uiState = uiState
        )

        HomeViewUiState.Error -> ErrorView(modifier = modifier.fillMaxSize())

        HomeViewUiState.Loading -> LoadingView(modifier = modifier.fillMaxSize())
    }
}

@Composable
private fun SuccessView(uiState: HomeViewUiState.Success, modifier: Modifier = Modifier) {
    GestureList(uiState)
}

@Composable
fun GestureList(
    uiState: HomeViewUiState.Success,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.padding_medium))
    ) {
        if (uiState.gestures != null) {
            itemsIndexed(
                items = uiState.gestures,
                key = {index, gesture -> gesture.id}
            ) { index, gesture ->
                Row(
                ) {
                    Text(gesture.name)
                    Spacer(Modifier.width(dimensionResource(R.dimen.padding_medium)))
                    Text(gesture.description)
                }
            }
        }
    }
}

@Composable
private fun ErrorView(modifier: Modifier = Modifier) {
    Text("Error")
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    Text("Loading")
}