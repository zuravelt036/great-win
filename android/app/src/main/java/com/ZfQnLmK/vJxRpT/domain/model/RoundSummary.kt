package com.ZfQnLmK.vJxRpT.domain.model

data class RoundSummary(
    val scheme: Scheme,
    val outcome: GameOutcome,
    val board: ScoreBoard,
    val ringState: RingState,
    val energized: List<Set<Int>>,
    val reachedTargets: Set<Int>
)
