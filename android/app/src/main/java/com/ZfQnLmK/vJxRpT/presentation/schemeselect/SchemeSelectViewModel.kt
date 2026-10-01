package com.ZfQnLmK.vJxRpT.presentation.schemeselect

import androidx.lifecycle.ViewModel
import com.ZfQnLmK.vJxRpT.domain.repository.ProgressRepository
import com.ZfQnLmK.vJxRpT.domain.usecase.GetSchemesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SchemeSelectViewModel(
    private val getSchemes: GetSchemesUseCase,
    private val progress: ProgressRepository
) : ViewModel() {

    private val state = MutableStateFlow(SchemeSelectUiState())
    val uiState: StateFlow<SchemeSelectUiState> = state.asStateFlow()

    fun refresh() {
        val selected = progress.activeSchemeId()
        val cards = getSchemes().map { scheme ->
            val stored = progress.progressFor(scheme.id)
            SchemeCardModel(
                id = scheme.id,
                name = scheme.name,
                segments = scheme.segments,
                moveLimit = scheme.moveLimit,
                difficulty = scheme.difficulty,
                previewIndex = scheme.previewIndex,
                stars = stored.stars,
                unlocked = stored.unlocked
            )
        }
        state.value = SchemeSelectUiState(cards, selected)
    }

    fun select(id: String) {
        state.value = state.value.copy(selectedId = id)
    }

    fun confirmSelection(): String {
        val id = state.value.selectedId
        progress.setActiveSchemeId(id)
        return id
    }
}
