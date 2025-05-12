package com.google.mediapipe.examples.handlandmarker

import android.app.Application
import android.graphics.PointF
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.mutableStateOf

@HiltViewModel
class NailUIViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _nailUiState = mutableStateOf(NailUIState())
    val nailUiState: State<NailUIState> = _nailUiState

    fun onEvent(event: NailUIEvent) {
        when (event) {
            is NailUIEvent.SetFingerData -> {
                when (event.index) {
                    4 -> {
                        _nailUiState.value = _nailUiState.value.copy(thumb = event.finger)
                    }

                    5 -> {
                        _nailUiState.value = _nailUiState.value.copy(thumb2 = event.finger)
                    }

                    8 -> {
                        _nailUiState.value = _nailUiState.value.copy(finger1a = event.finger)
                    }

                    9 -> {
                        _nailUiState.value = _nailUiState.value.copy(finger1b = event.finger)
                    }


                    12 -> {
                        _nailUiState.value = _nailUiState.value.copy(finger2a = event.finger)
                    }


                    13 -> {
                    _nailUiState.value = _nailUiState.value.copy(finger2b = event.finger)
                    }

                    16 -> {
                        _nailUiState.value = _nailUiState.value.copy(finger3a = event.finger)
                    }

                    17 -> {
                        _nailUiState.value = _nailUiState.value.copy(finger3b = event.finger)
                    }

                    20 -> {
                        _nailUiState.value = _nailUiState.value.copy(finger4a = event.finger)
                    }

                    21 -> {
                        _nailUiState.value = _nailUiState.value.copy(finger4b = event.finger)
                    }
                }
            }
        }
    }

}


data class NailUIState(
    var finger1a: PointF? = null,
    var finger1b: PointF? = null,
    var finger2a: PointF? = null,
    var finger2b: PointF? = null,
    var finger3a: PointF? = null,
    var finger3b: PointF? = null,
    var finger4a: PointF? = null,
    var finger4b: PointF? = null,
    var thumb: PointF? = null,
    var thumb2: PointF? = null
)

sealed class NailUIEvent {
    data class SetFingerData(
        var index: Int,
        var finger: PointF,
    ) : NailUIEvent()
}