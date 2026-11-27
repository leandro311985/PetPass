package com.leandro.petpass.presentation.exams

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.leandro.petpass.domain.model.ExamRecord
import com.leandro.petpass.domain.usecase.ObserveExamsUseCase
import com.leandro.petpass.domain.usecase.SaveExamUseCase
import com.leandro.petpass.presentation.common.toDateDisplayFormat
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExamViewModel(
    private val petId: String,
    private val observeExams: ObserveExamsUseCase,
    private val saveExam: SaveExamUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExamUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                observeExams(petId).collect { exams ->
                    _uiState.update { it.copy(exams = exams) }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e("ExamViewModel", "Failed to observe exams", exception)
                _uiState.update {
                    it.copy(errorMessage = "Não foi possível carregar o histórico de exames.")
                }
            }
        }
    }

    fun onEvent(event: ExamEvent) {
        when (event) {
            is ExamEvent.NameChanged -> updateState { copy(name = event.value) }
            is ExamEvent.PerformedAtChanged ->
                updateState { copy(performedAt = event.value.filter(Char::isDigit).take(8)) }
            is ExamEvent.NextDueDateChanged ->
                updateState { copy(nextDueDate = event.value.filter(Char::isDigit).take(8)) }
            is ExamEvent.VeterinarianChanged ->
                updateState { copy(veterinarian = event.value) }
            is ExamEvent.ResultChanged -> updateState { copy(result = event.value) }
            ExamEvent.Save -> save()
        }
    }

    private fun updateState(update: ExamUiState.() -> ExamUiState) {
        _uiState.update { it.update().copy(errorMessage = null, successMessage = null) }
    }

    private fun save() {
        val state = _uiState.value
        if (state.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            try {
                saveExam(
                    ExamRecord(
                        id = UUID.randomUUID().toString(),
                        petId = petId,
                        name = state.name.trim(),
                        performedAt = state.performedAt.toDateDisplayFormat(),
                        veterinarian = state.veterinarian.trim().ifBlank { null },
                        result = state.result.trim().ifBlank { null },
                        nextDueDate = state.nextDueDate.toDateDisplayFormat().ifBlank { null }
                    )
                )
                _uiState.update {
                    it.copy(
                        name = "",
                        performedAt = "",
                        nextDueDate = "",
                        veterinarian = "",
                        result = "",
                        isSaving = false,
                        successMessage = "Exame registrado."
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: IllegalArgumentException) {
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = exception.message)
                }
            } catch (exception: Exception) {
                Log.e("ExamViewModel", "Failed to save exam", exception)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Não foi possível salvar o exame. Tente novamente."
                    )
                }
            }
        }
    }

    class Factory(
        private val petId: String,
        private val observeExams: ObserveExamsUseCase,
        private val saveExam: SaveExamUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (!modelClass.isAssignableFrom(ExamViewModel::class.java)) {
                throw IllegalArgumentException("ViewModel não suportada: ${modelClass.name}")
            }
            return ExamViewModel(petId, observeExams, saveExam) as T
        }
    }
}
