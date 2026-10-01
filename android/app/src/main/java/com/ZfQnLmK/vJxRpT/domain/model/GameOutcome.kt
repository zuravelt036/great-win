package com.ZfQnLmK.vJxRpT.domain.model

enum class GameOutcome {
    WIN,
    OUT_OF_MOVES,
    OUT_OF_CHECKS,
    TIME_UP;

    val isWin: Boolean
        get() = this == WIN
}
