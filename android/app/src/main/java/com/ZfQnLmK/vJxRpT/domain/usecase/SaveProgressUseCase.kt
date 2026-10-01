package com.ZfQnLmK.vJxRpT.domain.usecase

import com.ZfQnLmK.vJxRpT.domain.repository.ProgressRepository

class SaveProgressUseCase(private val repository: ProgressRepository) {
    operator fun invoke(schemeId: String, stars: Int, score: Int) {
        repository.saveResult(schemeId, stars, score)
    }
}
