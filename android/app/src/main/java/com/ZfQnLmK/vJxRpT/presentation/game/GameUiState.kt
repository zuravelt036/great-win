package com.ZfQnLmK.vJxRpT.presentation.game

import com.ZfQnLmK.vJxRpT.domain.model.GameOutcome
import com.ZfQnLmK.vJxRpT.domain.model.RingState
import com.ZfQnLmK.vJxRpT.domain.model.Scheme

data class GameUiState(
    val scheme: Scheme,
    val ringState: RingState,
    val baseConductors: List<Set<Int>>,
    val energized: List<Set<Int>>,
    val reached: Set<Int>,
    val movesLeft: Int,
    val checksLeft: Int,
    val score: Int,
    val phase: GamePhase,
    val animatedRing: Int,
    val shakeTick: Int,
    val outcome: GameOutcome?
) {
    val inputEnabled: Boolean
        get() = phase == GamePhase.IDLE && outcome == null
}
