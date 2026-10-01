package com.ZfQnLmK.vJxRpT.domain.model

data class Scheme(
    val id: String,
    val name: String,
    val segments: Int,
    val moveLimit: Int,
    val difficulty: Int,
    val previewIndex: Int,
    val rings: List<RingSpec>
) {
    val targetIndices: List<Int>
        get() = listOf(0, segments / 4, segments / 2, segments * 3 / 4)

    val solutionSteps: Int
        get() = rings.sumOf { it.solutionStep }
}
