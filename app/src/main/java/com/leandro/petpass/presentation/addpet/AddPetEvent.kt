package com.leandro.petpass.presentation.addpet

sealed interface AddPetEvent {
    data class NameChanged(val value: String) : AddPetEvent
    data class SpeciesChanged(val value: String) : AddPetEvent
    data class BreedChanged(val value: String) : AddPetEvent
    data class BirthDateChanged(val value: String) : AddPetEvent
    data class GenderChanged(val value: String) : AddPetEvent
    data class RgaChanged(val value: String) : AddPetEvent
    data class MicrochipChanged(val value: String) : AddPetEvent
    data class OwnerNameChanged(val value: String) : AddPetEvent
    data class EmergencyPhoneChanged(val value: String) : AddPetEvent
    data class PhotoSelected(val uri: String?) : AddPetEvent
    data object Save : AddPetEvent
}
