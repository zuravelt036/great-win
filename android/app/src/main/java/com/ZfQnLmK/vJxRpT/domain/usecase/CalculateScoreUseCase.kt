package com.ZfQnLmK.vJxRpT.domain.usecase

import com.ZfQnLmK.vJxRpT.domain.model.GameOutcome
import com.ZfQnLmK.vJxRpT.domain.model.Scheme
import com.ZfQnLmK.vJxRpT.domain.model.ScoreBoard

class CalculateScoreUseCase {

    operator fun invoke(
        scheme: Scheme,
        outcome: GameOutcome,
        movesLeft: Int,
        checksLeft: Int,
        reachedTargets: Int
    ): ScoreBoard {
        val total = scheme.targetIndices.size
        val safeMoves = movesLeft.coerceAtLeast(0)
        val safeChecks = checksLeft.coerceAtLeast(0)
        val score = if (outcome.isWin) {
            BASE_WIN + safeMoves * MOVE_BONUS + safeChecks * CHECK_BONUS
        } else {
            reachedTargets * TARGET_BONUS + safeMoves * MOVE_CONSOLATION
        }
        val stars = if (!outcome.isWin) {
            0
        } else {
            val ratio = if (scheme.moveLimit > 0) safeMoves.toFloat() / scheme.moveLimit else 0f
            when {
                ratio >= GOLD_RATIO -> 3
                ratio >= SILVER_RATIO -> 2
                else -> 1
            }
        }
        return ScoreBoard(score, stars, safeMoves, safeChecks, reachedTargets, total)
    }

    private companion object {
        const val BASE_WIN = 600
        const val MOVE_BONUS = 80
        const val CHECK_BONUS = 120
        const val TARGET_BONUS = 90
        const val MOVE_CONSOLATION = 20
        const val GOLD_RATIO = 0.4f
        const val SILVER_RATIO = 0.15f
    }
}
