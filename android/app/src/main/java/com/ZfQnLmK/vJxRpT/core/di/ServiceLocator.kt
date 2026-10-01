package com.ZfQnLmK.vJxRpT.core.di

import android.content.Context
import com.ZfQnLmK.vJxRpT.data.local.PreferencesStorage
import com.ZfQnLmK.vJxRpT.data.repository.ProgressRepositoryImpl
import com.ZfQnLmK.vJxRpT.data.repository.SchemeRepositoryImpl
import com.ZfQnLmK.vJxRpT.domain.model.RoundSummary
import com.ZfQnLmK.vJxRpT.domain.repository.ProgressRepository
import com.ZfQnLmK.vJxRpT.domain.repository.SchemeRepository
import com.ZfQnLmK.vJxRpT.domain.usecase.CalculateScoreUseCase
import com.ZfQnLmK.vJxRpT.domain.usecase.CheckCircuitUseCase
import com.ZfQnLmK.vJxRpT.domain.usecase.GetSchemesUseCase
import com.ZfQnLmK.vJxRpT.domain.usecase.RotateRingUseCase
import com.ZfQnLmK.vJxRpT.domain.usecase.SaveProgressUseCase

object ServiceLocator {

    private var schemes: SchemeRepository? = null
    private var progress: ProgressRepository? = null

    var lastRound: RoundSummary? = null
    var pendingSchemeId: String = ""

    val rotateRing: RotateRingUseCase = RotateRingUseCase()
    val checkCircuit: CheckCircuitUseCase = CheckCircuitUseCase()
    val calculateScore: CalculateScoreUseCase = CalculateScoreUseCase()

    fun init(context: Context) {
        val app = context.applicationContext
        val schemeRepo = schemes ?: SchemeRepositoryImpl().also { schemes = it }
        if (progress == null) {
            progress = ProgressRepositoryImpl(PreferencesStorage(app), schemeRepo)
        }
    }

    fun schemeRepository(context: Context): SchemeRepository {
        init(context)
        return schemes ?: SchemeRepositoryImpl().also { schemes = it }
    }

    fun progressRepository(context: Context): ProgressRepository {
        init(context)
        val existing = progress
        if (existing != null) return existing
        val created = ProgressRepositoryImpl(
            PreferencesStorage(context.applicationContext),
            schemeRepository(context)
        )
        progress = created
        return created
    }

    fun getSchemes(context: Context): GetSchemesUseCase = GetSchemesUseCase(schemeRepository(context))

    fun saveProgress(context: Context): SaveProgressUseCase =
        SaveProgressUseCase(progressRepository(context))
}
