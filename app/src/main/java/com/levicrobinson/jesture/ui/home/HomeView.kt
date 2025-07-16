package com.levicrobinson.jesture.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
    Text("Hi")
}

@Composable
private fun ErrorView(modifier: Modifier = Modifier) {
    Text("Error")
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    Text("Loading")
}