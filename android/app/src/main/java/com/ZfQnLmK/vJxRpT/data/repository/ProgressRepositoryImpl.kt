package com.ZfQnLmK.vJxRpT.data.repository

import com.ZfQnLmK.vJxRpT.data.local.PreferencesStorage
import com.ZfQnLmK.vJxRpT.domain.model.SchemeProgress
import com.ZfQnLmK.vJxRpT.domain.repository.ProgressRepository
import com.ZfQnLmK.vJxRpT.domain.repository.SchemeRepository

class ProgressRepositoryImpl(
    private val storage: PreferencesStorage,
    private val schemes: SchemeRepository
) : ProgressRepository {

    override fun progressFor(schemeId: String): SchemeProgress {
        val list = schemes.schemes()
        val index = list.indexOfFirst { it.id == schemeId }
        val unlocked = index <= 0 || solvedBefore(index)
        return SchemeProgress(
            schemeId = schemeId,
            stars = storage.stars(schemeId),
            bestScore = storage.bestScore(schemeId),
            unlocked = unlocked
        )
    }

    override fun allProgress(): List<SchemeProgress> = schemes.schemes().map { progressFor(it.id) }

    override fun saveResult(schemeId: String, stars: Int, score: Int) {
        storage.storeResult(schemeId, stars, score)
    }

    override fun solvedCount(): Int = schemes.schemes().count { storage.stars(it.id) > 0 }

    override fun bestStarAverage(): Float {
        val list = schemes.schemes().map { storage.stars(it.id) }.filter { it > 0 }
        if (list.isEmpty()) return 0f
        return list.sum().toFloat() / list.size
    }

    override fun levelLabel(): String {
        val solved = solvedCount() + 1
        return if (solved < 10) "0" + solved else solved.toString()
    }

    override fun activeSchemeId(): String = storage.activeSchemeId(schemes.firstScheme().id)

    override fun setActiveSchemeId(id: String) = storage.setActiveSchemeId(id)

    override fun soundEnabled(): Boolean = storage.flag(PreferencesStorage.KEY_SOUND, true)

    override fun setSoundEnabled(value: Boolean) = storage.setFlag(PreferencesStorage.KEY_SOUND, value)

    override fun vibrationEnabled(): Boolean = storage.flag(PreferencesStorage.KEY_VIBRATION, true)

    override fun setVibrationEnabled(value: Boolean) =
        storage.setFlag(PreferencesStorage.KEY_VIBRATION, value)

    override fun highContrastEnabled(): Boolean = storage.flag(PreferencesStorage.KEY_CONTRAST, false)

    override fun setHighContrastEnabled(value: Boolean) =
        storage.setFlag(PreferencesStorage.KEY_CONTRAST, value)

    private fun solvedBefore(index: Int): Boolean {
        val list = schemes.schemes()
        for (i in 0 until index) {
            val id = list.getOrNull(i)?.id ?: return false
            if (storage.stars(id) <= 0) return false
        }
        return true
    }
}
