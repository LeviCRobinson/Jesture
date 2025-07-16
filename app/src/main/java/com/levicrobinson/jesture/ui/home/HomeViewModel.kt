package com.levicrobinson.jesture.ui.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levicrobinson.jesture.data.LOG_TAG
import com.levicrobinson.jesture.domain.model.Gesture
import com.levicrobinson.jesture.domain.usecase.GestureUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

sealed interface HomeViewUiState {
    data class Success(
        val gestures: List<Gesture>? = null
    ): HomeViewUiState
    data object Error: HomeViewUiState
    data object Loading: HomeViewUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gestureUseCases: GestureUseCases
) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeViewUiState> = MutableStateFlow(HomeViewUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var _gestures: MutableStateFlow<List<Gesture>?> = MutableStateFlow(null)
    private var _searchString = mutableStateOf("")

    init {
        viewModelScope.launch {
            initialize()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun initialize() {
        viewModelScope.launch {
            try {
                val searchStringFlow = snapshotFlow { _searchString }.mapLatest { it }
                if (_gestures.value == null) {
                    fetchList()
                }
                combine(
                    _gestures,
                    searchStringFlow
                ) { gestures, searchString ->
                    if(gestures == null) {
                        HomeViewUiState.Loading
                    } else {
                        HomeViewUiState.Success(
                            gestures = gestures
                        )
                    }
                }.onStart {
                    _uiState.value = HomeViewUiState.Loading
                }.onEmpty {
                    _uiState.value = HomeViewUiState.Success()
                }.onCompletion {
                    if(_uiState.value is HomeViewUiState.Loading) {
                        _uiState.value = HomeViewUiState.Success()
                    }
                }.catch { e ->
                    if (e !is CancellationException) {
                        Log.e(LOG_TAG, e.message ?: "Error while loading Gallery View data.")
                    } else {
                        Log.e(LOG_TAG, "GalleryViewModel CancellationException: " + e.message)
                    }
                    _uiState.value = HomeViewUiState.Error
                }.stateIn(
                    scope = viewModelScope,
                    SharingStarted.WhileSubscribed(5_000),
                    initialValue = HomeViewUiState.Loading
                ).collect { combinedState ->
                    _uiState.value = combinedState
                }
            } catch (e: CancellationException) {
                Log.e(LOG_TAG, "HomeViewModel CancellationException: " + e.message)
            } catch (e: Exception) {
                Log.e(LOG_TAG, e.message ?: "Error while loading Home view.")
                _uiState.value = HomeViewUiState.Error
            }
        }
    }

    fun fetchList() {
        viewModelScope.launch {
            _gestures.value = gestureUseCases.fetchGestures()
        }
    }

    fun submitGestureCreation(gesture: Gesture) {
        viewModelScope.launch {
            val response = gestureUseCases.createGesture(gesture)
            response?.let {
                println("${response.name} created")
                _gestures.value = gestureUseCases.fetchGestures()
            }
        }
    }

    fun deleteGesture(gesture: Gesture) {
        viewModelScope.launch {
            val response = gestureUseCases.deleteGesture(gesture)
            response?.let {
                println("${gesture.name} deleted")
                _gestures.value = gestureUseCases.fetchGestures()
            }
        }
    }
}