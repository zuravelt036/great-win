package com.ZfQnLmK.vJxRpT.domain.model

data class ScoreBoard(
    val score: Int,
    val stars: Int,
    val movesLeft: Int,
    val checksLeft: Int,
    val reachedTargets: Int,
    val totalTargets: Int
)
