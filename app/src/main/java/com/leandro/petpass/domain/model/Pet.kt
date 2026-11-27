package com.leandro.petpass.domain.model

data class Pet(
    val id: String,
    val name: String,
    val species: String,
    val breed: String,
    val birthDate: String,
    val gender: String,
    val rga: String,
    val microchip: String,
    val ownerName: String,
    val emergencyPhone: String,
    val photoPath: String?
)
