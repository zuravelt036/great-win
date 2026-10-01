package com.ZfQnLmK.vJxRpT.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ZfQnLmK.vJxRpT.core.config.GameConfig
import com.ZfQnLmK.vJxRpT.domain.model.GameOutcome
import com.ZfQnLmK.vJxRpT.domain.model.RingState
import com.ZfQnLmK.vJxRpT.domain.model.RoundSummary
import com.ZfQnLmK.vJxRpT.domain.model.Scheme
import com.ZfQnLmK.vJxRpT.domain.repository.ProgressRepository
import com.ZfQnLmK.vJxRpT.domain.repository.SchemeRepository
import com.ZfQnLmK.vJxRpT.domain.usecase.CalculateScoreUseCase
import com.ZfQnLmK.vJxRpT.domain.usecase.CheckCircuitUseCase
import com.ZfQnLmK.vJxRpT.domain.usecase.RotateRingUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val schemes: SchemeRepository,
    private val progress: ProgressRepository,
    private val rotateRing: RotateRingUseCase,
    private val checkCircuit: CheckCircuitUseCase,
    private val calculateScore: CalculateScoreUseCase,
    schemeId: String
) : ViewModel() {

    private val scheme: Scheme = if (schemeId.isEmpty()) {
        schemes.schemeById(progress.activeSchemeId())
    } else {
        schemes.schemeById(schemeId)
    }

    private val mountedAt = System.currentTimeMillis()
    private var backstopJob: Job? = null
    private var resolveJob: Job? = null
    private var checkJob: Job? = null
    private var engaged = false

    private val state = MutableStateFlow(buildInitialState())
    val uiState: StateFlow<GameUiState> = state.asStateFlow()

    init {
        progress.setActiveSchemeId(scheme.id)
        armIdleBackstop()
    }

    private fun buildInitialState(): GameUiState {
        val ringState = RingState.initial(scheme.rings.size)
        val circuit = checkCircuit(scheme, ringState)
        return GameUiState(
            scheme = scheme,
            ringState = ringState,
            baseConductors = checkCircuit.baseConductors(scheme),
            energized = circuit.energized,
            reached = circuit.reachedTargets,
            movesLeft = scheme.moveLimit,
            checksLeft = GameConfig.MAX_CHECKS,
            score = potentialScore(scheme.moveLimit, GameConfig.MAX_CHECKS),
            phase = GamePhase.IDLE,
            animatedRing = -1,
            shakeTick = 0,
            outcome = null
        )
    }

    private fun potentialScore(movesLeft: Int, checksLeft: Int): Int =
        BASE_SCORE + movesLeft * MOVE_WEIGHT + checksLeft * CHECK_WEIGHT

    fun rotate(ring: Int, clockwise: Boolean) {
        val current = state.value
        if (!current.inputEnabled || current.movesLeft <= 0) return
        markEngaged()
        val delta = if (clockwise) 1 else -1
        val nextRings = rotateRing(current.ringState, ring, delta, scheme.segments)
        val circuit = checkCircuit(scheme, nextRings)
        val movesLeft = current.movesLeft - 1
        state.value = current.copy(
            ringState = nextRings,
            energized = circuit.energized,
            reached = circuit.reachedTargets,
            movesLeft = movesLeft,
            score = potentialScore(movesLeft, current.checksLeft),
            animatedRing = ring,
            phase = GamePhase.IDLE
        )
        if (movesLeft <= 0) {
            scheduleResolve(GameOutcome.OUT_OF_MOVES, GamePhase.LOSE, GameConfig.LOSE_HOLD_MS)
        }
    }

    fun runCheck() {
        val current = state.value
        if (!current.inputEnabled) return
        markEngaged()
        val circuit = checkCircuit(scheme, current.ringState)
        state.value = current.copy(
            phase = GamePhase.CHECKING,
            energized = circuit.energized,
            reached = circuit.reachedTargets,
            animatedRing = -1
        )
        checkJob?.cancel()
        checkJob = viewModelScope.launch {
            delay(GameConfig.CHECK_ANIM_MS)
            if (circuit.isSolved) {
                scheduleResolve(GameOutcome.WIN, GamePhase.WIN, GameConfig.WIN_HOLD_MS)
            } else {
                val checksLeft = state.value.checksLeft - 1
                val failed = state.value
                state.value = failed.copy(
                    checksLeft = checksLeft,
                    score = potentialScore(failed.movesLeft, checksLeft),
                    shakeTick = failed.shakeTick + 1,
                    phase = if (checksLeft <= 0) GamePhase.LOSE else GamePhase.IDLE
                )
                if (checksLeft <= 0) {
                    scheduleResolve(GameOutcome.OUT_OF_CHECKS, GamePhase.LOSE, GameConfig.LOSE_HOLD_MS)
                }
            }
        }
    }

    fun restart() {
        backstopJob?.cancel()
        resolveJob?.cancel()
        checkJob?.cancel()
        engaged = false
        state.value = buildInitialState()
        armIdleBackstop()
    }

    private fun markEngaged() {
        if (engaged) return
        engaged = true
        val elapsed = System.currentTimeMillis() - mountedAt
        val wait = maxOf(GameConfig.ENGAGED_RESOLVE_MS, GameConfig.MIN_RESOLVE_FROM_MOUNT_MS - elapsed)
        backstopJob?.cancel()
        backstopJob = viewModelScope.launch {
            delay(wait)
            if (state.value.outcome == null) {
                scheduleResolve(GameOutcome.TIME_UP, GamePhase.LOSE, GameConfig.LOSE_HOLD_MS)
            }
        }
    }

    private fun armIdleBackstop() {
        backstopJob?.cancel()
        backstopJob = viewModelScope.launch {
            delay(GameConfig.IDLE_RESOLVE_MS)
            if (state.value.outcome == null) {
                scheduleResolve(GameOutcome.TIME_UP, GamePhase.LOSE, GameConfig.LOSE_HOLD_MS)
            }
        }
    }

    private fun scheduleResolve(outcome: GameOutcome, phase: GamePhase, hold: Long) {
        if (state.value.outcome != null) return
        resolveJob?.cancel()
        state.value = state.value.copy(phase = phase, animatedRing = -1)
        resolveJob = viewModelScope.launch {
            delay(hold)
            finish(outcome)
        }
    }

    private fun finish(outcome: GameOutcome) {
        val current = state.value
        if (current.outcome != null) return
        val board = calculateScore(
            scheme,
            outcome,
            current.movesLeft,
            current.checksLeft,
            current.reached.size
        )
        if (board.stars > 0) {
            progress.saveResult(scheme.id, board.stars, board.score)
        }
        state.value = current.copy(
            outcome = outcome,
            score = board.score,
            phase = if (outcome.isWin) GamePhase.WIN else GamePhase.LOSE
        )
    }

    fun summary(): RoundSummary {
        val current = state.value
        val outcome = current.outcome ?: GameOutcome.TIME_UP
        val board = calculateScore(
            scheme,
            outcome,
            current.movesLeft,
            current.checksLeft,
            current.reached.size
        )
        return RoundSummary(
            scheme = scheme,
            outcome = outcome,
            board = board,
            ringState = current.ringState,
            energized = current.energized,
            reachedTargets = current.reached
        )
    }

    override fun onCleared() {
        backstopJob?.cancel()
        resolveJob?.cancel()
        checkJob?.cancel()
        backstopJob = null
        resolveJob = null
        checkJob = null
        super.onCleared()
    }

    private companion object {
        const val BASE_SCORE = 600
        const val MOVE_WEIGHT = 80
        const val CHECK_WEIGHT = 120
    }
}
