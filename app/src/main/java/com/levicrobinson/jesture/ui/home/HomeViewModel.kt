package com.levicrobinson.jesture.ui.home

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levicrobinson.jesture.data.LOG_TAG
import com.levicrobinson.jesture.data.repository.SensorRepositoryImpl
import com.levicrobinson.jesture.domain.model.AccelerometerReading
import com.levicrobinson.jesture.domain.model.Gesture
import com.levicrobinson.jesture.domain.usecase.GestureUseCases
import com.levicrobinson.jesture.domain.usecase.StartGestureRecordUseCase
import com.levicrobinson.jesture.domain.usecase.StopGestureRecordUseCase
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
import kotlin.math.min

sealed interface HomeViewUiState {
    data class Success(
        val gestures: List<Gesture>? = null,
        val homeViewDialogType: HomeViewDialogType = HomeViewDialogType.NONE,
        val gestureDialogInputs: GestureDialogInputs = GestureDialogInputs(),
        val gestureReadings: List<AccelerometerReading>? = null
    ) : HomeViewUiState

    data object Error : HomeViewUiState
    data object Loading : HomeViewUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gestureUseCases: GestureUseCases,
    private val sensorRepositoryFactory: SensorRepositoryImpl.Factory
) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeViewUiState> =
        MutableStateFlow(HomeViewUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var _gestures: MutableStateFlow<List<Gesture>?> = MutableStateFlow(null)
    private var _searchString = mutableStateOf("")
    private var _readings: ArrayList<AccelerometerReading>? = null
    private lateinit var startGestureRecordUseCase: StartGestureRecordUseCase
    private lateinit var stopGestureRecordUseCase: StopGestureRecordUseCase

    init {
        viewModelScope.launch {
            initialize()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun initialize() {
        initializeGestureRecordUseCases()

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
                    if (gestures == null) {
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
                    if (_uiState.value is HomeViewUiState.Loading) {
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

    fun submitGestureCreation() {
        val currentState = _uiState.value
        if (currentState is HomeViewUiState.Success) {
            viewModelScope.launch {
                val response = gestureUseCases.createGesture(
                    currentState.gestureDialogInputs.gestureName,
                    currentState.gestureDialogInputs.gestureDescription,
                    currentState.gestureReadings ?: arrayListOf()
                )
                response?.let {
                    // On response, set dialog inputs and gesture readings to default state.
                    _gestures.value = gestureUseCases.fetchGestures()
                }
            }
        }
    }

    fun submitGestureRecognition(context: Context) {
        val currentState = _uiState.value
        if (currentState is HomeViewUiState.Success) {
            viewModelScope.launch {
                val response = gestureUseCases.recognizeGestureUseCase(
                    currentState.gestureReadings?.toList() ?: listOf()
                )
                response?.let {
                    // On response, set dialog inputs and gesture readings to default state.
                    Toast.makeText(context, "Recognized gesture: ${it.name}", Toast.LENGTH_SHORT).show()
                }
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

    fun updateDialogType(dialogType: HomeViewDialogType) {
        val currentState = _uiState.value
        if (currentState is HomeViewUiState.Success) {
            viewModelScope.launch {
                var updatedState = currentState.copy(homeViewDialogType = dialogType)
                if (dialogType == HomeViewDialogType.NONE) {
                    _readings = null
                    updatedState = updatedState.copy(
                        gestureDialogInputs = GestureDialogInputs(),
                        gestureReadings = null
                    )
                }

                _uiState.value = updatedState
            }
        }
    }

    fun updateDialogGestureName(gestureName: String) {
        val currentState = _uiState.value
        if (currentState is HomeViewUiState.Success) {
            val newGestureRecordInputs = currentState.gestureDialogInputs.copy(
                gestureName = gestureName.substring(0, min(gestureName.length, 11))
            )
            _uiState.value = currentState.copy(
                gestureDialogInputs = newGestureRecordInputs
            )
        }
    }

    fun updateDialogGestureDescription(gestureDescription: String) {
        val currentState = _uiState.value
        if (currentState is HomeViewUiState.Success) {
            val newGestureRecordInputs = currentState.gestureDialogInputs.copy(
                gestureDescription = gestureDescription
            )
            _uiState.value = currentState.copy(
                gestureDialogInputs = newGestureRecordInputs
            )
        }
    }

    fun startGestureRecord() {
        viewModelScope.launch {
            // In case previous readings exist, reset the arraylist to empty before recording
            _readings = arrayListOf()
            startGestureRecordUseCase()
        }
    }

    fun stopGestureRecord() {
        val currentState = _uiState.value
        if (currentState is HomeViewUiState.Success) {
            viewModelScope.launch {
                // Stop recording accelerometer readings
                stopGestureRecordUseCase()
                // Update UI State to included recorded readings now that recording is done.
                _uiState.value = currentState.copy(gestureReadings = _readings)
                // Reset readings to null
                _readings = null
            }
        }
    }

    fun canConfirmGestureCreation(): Boolean {
        val currentState = _uiState.value
        return if (currentState is HomeViewUiState.Success) {
            currentState.gestureDialogInputs.gestureName.isNotBlank() &&
                    currentState.gestureDialogInputs.gestureDescription.isNotBlank() &&
                    !currentState.gestureReadings.isNullOrEmpty()
        } else {
            false
        }
    }

    private fun initializeGestureRecordUseCases() {
        val sensorRepository = sensorRepositoryFactory.create(onReading = { x, y, z ->
            val reading = AccelerometerReading(
                accelX = x,
                accelY = y,
                accelZ = z
            )
            _readings?.add(reading)
        })
        startGestureRecordUseCase = StartGestureRecordUseCase(sensorRepository)
        stopGestureRecordUseCase = StopGestureRecordUseCase(sensorRepository)
    }
}

enum class HomeViewDialogType {
    NONE, DELETE_GESTURE, CREATE_GESTURE, UPDATE_GESTURE
}

data class GestureDialogInputs(
    val gestureName: String = "",
    val gestureDescription: String = ""
)