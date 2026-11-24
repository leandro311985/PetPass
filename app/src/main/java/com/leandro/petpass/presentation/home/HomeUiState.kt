package com.leandro.petpass.presentation.home

import com.leandro.petpass.presentation.rgCard.PetRgUiModel


data class HealthReminderUiModel(
    val id: String,
    val title: String,
    val dueDate: String,
    val category: String, // Ex: "Vacina", "Vermífugo", "Antipulgas"
    val isOverdue: Boolean = false
)

data class PetHomeUiModel(
    val rgData: PetRgUiModel,
    val currentWeightKg: Double?,
    val lastVetVisit: String,
    val medicalAlert: String?, // Ex: "Alérgico a Dipirona"
    val upcomingReminders: List<HealthReminderUiModel>
)
