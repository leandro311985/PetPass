package com.leandro.petpass.presentation.rgCard

data class PetRgUiModel(
    val id: String,
    val name: String,
    val species: String,
    val breed: String,
    val birthDate: String,
    val gender: String,
    val microchip: String,
    val ownerName: String,
    val emergencyPhone: String,
    val photoUrl: String? = null,
    val qrCodeUrl: String? = null
)
