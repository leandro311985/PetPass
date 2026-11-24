package com.leandro.petpass.domain.model

data class Vaccine(
    val id: String,
    val petId: String,
    val name: String,
    val administeredAt: String,
    val nextDueDate: String?,
    val veterinarian: String?,
    val notes: String?
)
