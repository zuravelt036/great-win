package com.ZfQnLmK.vJxRpT.domain.repository

import com.ZfQnLmK.vJxRpT.domain.model.SchemeProgress

interface ProgressRepository {
    fun progressFor(schemeId: String): SchemeProgress
    fun allProgress(): List<SchemeProgress>
    fun saveResult(schemeId: String, stars: Int, score: Int)
    fun solvedCount(): Int
    fun bestStarAverage(): Float
    fun levelLabel(): String
    fun activeSchemeId(): String
    fun setActiveSchemeId(id: String)
    fun soundEnabled(): Boolean
    fun setSoundEnabled(value: Boolean)
    fun vibrationEnabled(): Boolean
    fun setVibrationEnabled(value: Boolean)
    fun highContrastEnabled(): Boolean
    fun setHighContrastEnabled(value: Boolean)
}
