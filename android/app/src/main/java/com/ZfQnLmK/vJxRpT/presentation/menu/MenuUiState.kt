package com.ZfQnLmK.vJxRpT.presentation.menu

data class MenuUiState(
    val levelLabel: String = "01",
    val bestStars: Float = 0f,
    val solvedCount: Int = 0,
    val activeSchemeName: String = ""
) {
    val showStats: Boolean
        get() = bestStars > 0f && solvedCount > 0
}
