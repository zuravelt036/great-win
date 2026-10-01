package com.ZfQnLmK.vJxRpT.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ZfQnLmK.vJxRpT.core.config.GameConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val state = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = state.asStateFlow()

    private var timerJob: Job? = null
    private var tickerJob: Job? = null

    fun start() {
        if (timerJob != null) return
        startTicker()
        timerJob = viewModelScope.launch {
            delay(GameConfig.LOADER_DURATION_MS)
            state.value = state.value.copy(progressStage = STAGE_COUNT, finished = true)
        }
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            var stage = 0
            while (stage < STAGE_COUNT) {
                delay(GameConfig.LOADER_DURATION_MS / STAGE_COUNT)
                stage += 1
                if (!state.value.finished) {
                    state.value = state.value.copy(progressStage = stage)
                }
            }
        }
    }

    fun consumeNavigation() {
        state.value = state.value.copy(finished = false, navigated = true)
    }

    fun hasNavigated(): Boolean = state.value.navigated

    override fun onCleared() {
        timerJob?.cancel()
        timerJob = null
        tickerJob?.cancel()
        tickerJob = null
        super.onCleared()
    }

    private companion object {
        const val STAGE_COUNT = 4
    }
}
