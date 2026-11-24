package com.leandro.petpass.presentation.weight

import com.leandro.petpass.domain.model.WeightRecord

data class WeightUiState(
    val records: List<WeightRecord> = emptyList(),
    val weight: String = "",
    val measuredAt: String = "",
    val veterinarian: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
