package com.ZfQnLmK.vJxRpT.presentation.schemeselect

data class SchemeCardModel(
    val id: String,
    val name: String,
    val segments: Int,
    val moveLimit: Int,
    val difficulty: Int,
    val previewIndex: Int,
    val stars: Int,
    val unlocked: Boolean
)
