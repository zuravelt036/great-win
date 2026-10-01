package com.ZfQnLmK.vJxRpT.presentation.tutorial

import com.ZfQnLmK.vJxRpT.domain.model.RingState
import com.ZfQnLmK.vJxRpT.domain.model.Scheme
import com.ZfQnLmK.vJxRpT.domain.model.TutorialStep

data class TutorialUiState(
    val scheme: Scheme,
    val steps: List<TutorialStep>,
    val ringState: RingState,
    val baseConductors: List<Set<Int>>,
    val energized: List<Set<Int>>,
    val reached: Set<Int>,
    val solved: Boolean,
    val animatedRing: Int
)
