package com.leandro.petpass.domain.model

data class ExamRecord(
    val id: String,
    val petId: String,
    val name: String,
    val performedAt: String,
    val veterinarian: String?,
    val result: String?,
    val nextDueDate: String? = null,
    val reminderDismissed: Boolean = false
)
