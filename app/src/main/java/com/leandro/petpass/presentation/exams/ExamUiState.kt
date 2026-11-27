package com.leandro.petpass.presentation.exams

import com.leandro.petpass.domain.model.ExamRecord

data class ExamUiState(
    val exams: List<ExamRecord> = emptyList(),
    val name: String = "",
    val performedAt: String = "",
    val nextDueDate: String = "",
    val veterinarian: String = "",
    val result: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
