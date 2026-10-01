package com.ZfQnLmK.vJxRpT.core.ui

data class BoardRender(
    val segments: Int,
    val baseConductors: List<Set<Int>>,
    val offsets: List<Int>,
    val energized: List<Set<Int>>,
    val targets: List<Int>,
    val reached: Set<Int>,
    val mood: BoardMood
) {
    companion object {
        val EMPTY = BoardRender(8, emptyList(), emptyList(), emptyList(), emptyList(), emptySet(), BoardMood.IDLE)
    }
}
