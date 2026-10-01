package com.ZfQnLmK.vJxRpT.data.repository

import com.ZfQnLmK.vJxRpT.data.sample.SampleData
import com.ZfQnLmK.vJxRpT.domain.model.Scheme
import com.ZfQnLmK.vJxRpT.domain.model.TutorialStep
import com.ZfQnLmK.vJxRpT.domain.repository.SchemeRepository

class SchemeRepositoryImpl : SchemeRepository {

    private val all = SampleData.schemes

    override fun schemes(): List<Scheme> = all

    override fun schemeById(id: String): Scheme = all.firstOrNull { it.id == id } ?: firstScheme()

    override fun firstScheme(): Scheme = all.firstOrNull() ?: SampleData.tutorialScheme

    override fun nextScheme(id: String): Scheme {
        val index = all.indexOfFirst { it.id == id }
        if (index < 0) return firstScheme()
        val nextIndex = (index + 1) % all.size
        return all.getOrNull(nextIndex) ?: firstScheme()
    }

    override fun tutorialScheme(): Scheme = SampleData.tutorialScheme

    override fun tutorialSteps(): List<TutorialStep> = SampleData.tutorialSteps
}
