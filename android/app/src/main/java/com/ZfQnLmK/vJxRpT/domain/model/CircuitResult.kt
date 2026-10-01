package com.ZfQnLmK.vJxRpT.domain.model

data class CircuitResult(
    val energized: List<Set<Int>>,
    val reachedTargets: Set<Int>,
    val isSolved: Boolean
)
