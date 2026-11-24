package com.leandro.petpass.presentation.vaccines

import com.leandro.petpass.domain.model.Vaccine

data class VaccineUiState(
    val vaccines: List<Vaccine> = emptyList(),
    val name: String = "",
    val administeredAt: String = "",
    val nextDueDate: String = "",
    val veterinarian: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
