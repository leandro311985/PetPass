package com.leandro.petpass.presentation.weight

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.leandro.petpass.domain.model.WeightRecord
import com.leandro.petpass.domain.usecase.ObserveWeightRecordsUseCase
import com.leandro.petpass.domain.usecase.SaveWeightRecordUseCase
import com.leandro.petpass.presentation.common.toDateDisplayFormat
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeightViewModel(
    private val petId: String,
    private val observeWeightRecords: ObserveWeightRecordsUseCase,
    private val saveWeightRecord: SaveWeightRecordUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeightUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                observeWeightRecords(petId).collect { records ->
                    _uiState.update { it.copy(records = records) }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e("WeightViewModel", "Failed to observe weight records", exception)
                _uiState.update {
                    it.copy(errorMessage = "Não foi possível carregar o histórico de peso.")
                }
            }
        }
    }

    fun onEvent(event: WeightEvent) {
        when (event) {
            is WeightEvent.WeightChanged -> {
                val sanitized = event.value.replace(',', '.')
                    .filter { it.isDigit() || it == '.' }
                    .let { value ->
                        val decimalPoint = value.indexOf('.')
                        if (decimalPoint < 0) value
                        else value.substring(0, decimalPoint + 1) +
                            value.substring(decimalPoint + 1).replace(".", "")
                    }
                    .take(7)
                updateState { copy(weight = sanitized) }
            }
            is WeightEvent.MeasuredAtChanged ->
                updateState { copy(measuredAt = event.value.filter(Char::isDigit).take(8)) }
            is WeightEvent.VeterinarianChanged ->
                updateState { copy(veterinarian = event.value) }
            is WeightEvent.NotesChanged -> updateState { copy(notes = event.value) }
            WeightEvent.Save -> save()
        }
    }

    private fun updateState(update: WeightUiState.() -> WeightUiState) {
        _uiState.update { it.update().copy(errorMessage = null, successMessage = null) }
    }

    private fun save() {
        val state = _uiState.value
        if (state.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            try {
                val weight = state.weight.toDoubleOrNull()
                    ?: throw IllegalArgumentException("Informe um peso válido.")
                val record = WeightRecord(
                    id = UUID.randomUUID().toString(),
                    petId = petId,
                    weightKg = weight,
                    measuredAt = state.measuredAt.toDateDisplayFormat(),
                    veterinarian = state.veterinarian.trim().ifBlank { null },
                    notes = state.notes.trim().ifBlank { null }
                )
                saveWeightRecord(record)
                _uiState.update {
                    it.copy(
                        weight = "",
                        measuredAt = "",
                        veterinarian = "",
                        notes = "",
                        isSaving = false,
                        successMessage = "Peso registrado."
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: IllegalArgumentException) {
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = exception.message)
                }
            } catch (exception: Exception) {
                Log.e("WeightViewModel", "Failed to save weight record", exception)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Não foi possível salvar o peso. Tente novamente."
                    )
                }
            }
        }
    }

    class Factory(
        private val petId: String,
        private val observeWeightRecords: ObserveWeightRecordsUseCase,
        private val saveWeightRecord: SaveWeightRecordUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (!modelClass.isAssignableFrom(WeightViewModel::class.java)) {
                throw IllegalArgumentException("ViewModel não suportada: ${modelClass.name}")
            }
            return WeightViewModel(petId, observeWeightRecords, saveWeightRecord) as T
        }
    }
}
