package com.ZfQnLmK.vJxRpT.presentation.tutorial

import androidx.lifecycle.ViewModel
import com.ZfQnLmK.vJxRpT.domain.model.RingState
import com.ZfQnLmK.vJxRpT.domain.repository.SchemeRepository
import com.ZfQnLmK.vJxRpT.domain.usecase.CheckCircuitUseCase
import com.ZfQnLmK.vJxRpT.domain.usecase.RotateRingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TutorialViewModel(
    private val schemes: SchemeRepository,
    private val rotateRing: RotateRingUseCase,
    private val checkCircuit: CheckCircuitUseCase
) : ViewModel() {

    private val scheme = schemes.tutorialScheme()

    private val state = MutableStateFlow(initialState())
    val uiState: StateFlow<TutorialUiState> = state.asStateFlow()

    private fun initialState(): TutorialUiState {
        val rings = RingState(listOf(0, 0, 0))
        val circuit = checkCircuit(scheme, rings)
        return TutorialUiState(
            scheme = scheme,
            steps = schemes.tutorialSteps(),
            ringState = rings,
            baseConductors = checkCircuit.baseConductors(scheme),
            energized = circuit.energized,
            reached = circuit.reachedTargets,
            solved = circuit.isSolved,
            animatedRing = -1
        )
    }

    fun rotate(ring: Int, clockwise: Boolean) {
        val current = state.value
        val delta = if (clockwise) 1 else -1
        val next = rotateRing(current.ringState, ring, delta, scheme.segments)
        val circuit = checkCircuit(scheme, next)
        state.value = current.copy(
            ringState = next,
            energized = circuit.energized,
            reached = circuit.reachedTargets,
            solved = circuit.isSolved,
            animatedRing = ring
        )
    }
}
