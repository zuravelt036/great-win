package com.ZfQnLmK.vJxRpT.domain.repository

import com.ZfQnLmK.vJxRpT.domain.model.Scheme
import com.ZfQnLmK.vJxRpT.domain.model.TutorialStep

interface SchemeRepository {
    fun schemes(): List<Scheme>
    fun schemeById(id: String): Scheme
    fun firstScheme(): Scheme
    fun nextScheme(id: String): Scheme
    fun tutorialScheme(): Scheme
    fun tutorialSteps(): List<TutorialStep>
}
