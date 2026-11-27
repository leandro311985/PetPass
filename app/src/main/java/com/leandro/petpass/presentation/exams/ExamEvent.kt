package com.leandro.petpass.presentation.exams

sealed interface ExamEvent {
    data class NameChanged(val value: String) : ExamEvent
    data class PerformedAtChanged(val value: String) : ExamEvent
    data class NextDueDateChanged(val value: String) : ExamEvent
    data class VeterinarianChanged(val value: String) : ExamEvent
    data class ResultChanged(val value: String) : ExamEvent
    data object Save : ExamEvent
}
