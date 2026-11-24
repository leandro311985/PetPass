package com.leandro.petpass.presentation.vaccines

sealed interface VaccineEvent {
    data class NameChanged(val value: String) : VaccineEvent
    data class AdministeredAtChanged(val value: String) : VaccineEvent
    data class NextDueDateChanged(val value: String) : VaccineEvent
    data class VeterinarianChanged(val value: String) : VaccineEvent
    data class NotesChanged(val value: String) : VaccineEvent
    data object Save : VaccineEvent
}
