package com.leandro.petpass.domain.model

data class WeightRecord(
    val id: String,
    val petId: String,
    val weightKg: Double,
    val measuredAt: String,
    val veterinarian: String?,
    val notes: String?
)
