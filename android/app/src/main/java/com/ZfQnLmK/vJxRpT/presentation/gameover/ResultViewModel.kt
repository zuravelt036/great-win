package com.ZfQnLmK.vJxRpT.presentation.gameover

import androidx.lifecycle.ViewModel
import com.ZfQnLmK.vJxRpT.core.ui.BoardMood
import com.ZfQnLmK.vJxRpT.domain.model.RoundSummary
import com.ZfQnLmK.vJxRpT.domain.repository.ProgressRepository
import com.ZfQnLmK.vJxRpT.domain.repository.SchemeRepository
import com.ZfQnLmK.vJxRpT.domain.usecase.CheckCircuitUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ResultViewModel(
    private val summary: RoundSummary?,
    private val schemes: SchemeRepository,
    private val progress: ProgressRepository,
    private val checkCircuit: CheckCircuitUseCase
) : ViewModel() {

    private val state = MutableStateFlow(build())
    val uiState: StateFlow<ResultUiState> = state.asStateFlow()

    private fun build(): ResultUiState {
        val round = summary
        if (round == null) {
            return ResultUiState(hasRound = false)
        }
        return ResultUiState(
            hasRound = true,
            isWin = round.outcome.isWin,
            schemeName = round.scheme.name,
            segments = round.scheme.segments,
            stars = round.board.stars,
            movesLeft = round.board.movesLeft,
            reachedTargets = round.board.reachedTargets,
            totalTargets = round.board.totalTargets,
            score = round.board.score,
            segmentsCount = round.scheme.segments,
            baseConductors = checkCircuit.baseConductors(round.scheme),
            offsets = round.ringState.offsets,
            energized = round.energized,
            targets = round.scheme.targetIndices,
            reached = round.reachedTargets,
            mood = if (round.outcome.isWin) BoardMood.WIN else BoardMood.LOSE
        )
    }

    fun currentSchemeId(): String = summary?.scheme?.id ?: progress.activeSchemeId()

    fun nextSchemeId(): String = schemes.nextScheme(currentSchemeId()).id
}
