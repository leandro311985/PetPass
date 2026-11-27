package com.leandro.petpass.presentation.addpet

data class AddPetUiState(
    val isEditing: Boolean = false,
    val name: String = "",
    val species: String = "",
    val breed: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val rga: String = "",
    val microchip: String = "",
    val ownerName: String = "",
    val emergencyPhone: String = "",
    val photoUri: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)
