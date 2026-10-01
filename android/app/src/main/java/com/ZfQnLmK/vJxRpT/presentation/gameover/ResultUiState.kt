package com.ZfQnLmK.vJxRpT.presentation.gameover

import com.ZfQnLmK.vJxRpT.core.ui.BoardMood

data class ResultUiState(
    val hasRound: Boolean = false,
    val isWin: Boolean = false,
    val schemeName: String = "",
    val segments: Int = 0,
    val stars: Int = 0,
    val movesLeft: Int = 0,
    val reachedTargets: Int = 0,
    val totalTargets: Int = 0,
    val score: Int = 0,
    val segmentsCount: Int = 0,
    val baseConductors: List<Set<Int>> = emptyList(),
    val offsets: List<Int> = emptyList(),
    val energized: List<Set<Int>> = emptyList(),
    val targets: List<Int> = emptyList(),
    val reached: Set<Int> = emptySet(),
    val mood: BoardMood = BoardMood.IDLE
)
