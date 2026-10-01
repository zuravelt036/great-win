package com.ZfQnLmK.vJxRpT.presentation.menu

import androidx.lifecycle.ViewModel
import com.ZfQnLmK.vJxRpT.domain.repository.ProgressRepository
import com.ZfQnLmK.vJxRpT.domain.repository.SchemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel(
    private val progress: ProgressRepository,
    private val schemes: SchemeRepository
) : ViewModel() {

    private val state = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = state.asStateFlow()

    fun refresh() {
        val activeId = progress.activeSchemeId()
        state.value = MenuUiState(
            levelLabel = progress.levelLabel(),
            bestStars = progress.bestStarAverage(),
            solvedCount = progress.solvedCount(),
            activeSchemeName = schemes.schemeById(activeId).name
        )
    }

    fun activeSchemeId(): String = progress.activeSchemeId()
}
