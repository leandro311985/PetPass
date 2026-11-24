package com.leandro.petpass.presentation.weight

sealed interface WeightEvent {
    data class WeightChanged(val value: String) : WeightEvent
    data class MeasuredAtChanged(val value: String) : WeightEvent
    data class VeterinarianChanged(val value: String) : WeightEvent
    data class NotesChanged(val value: String) : WeightEvent
    data object Save : WeightEvent
}
