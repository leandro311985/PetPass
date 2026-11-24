package com.leandro.petpass.presentation.vaccines

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.leandro.petpass.domain.model.Vaccine
import com.leandro.petpass.domain.usecase.ObserveVaccinesUseCase
import com.leandro.petpass.domain.usecase.SaveVaccineUseCase
import com.leandro.petpass.presentation.common.toDateDisplayFormat
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VaccineViewModel(
    private val petId: String,
    private val observeVaccines: ObserveVaccinesUseCase,
    private val saveVaccine: SaveVaccineUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(VaccineUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                observeVaccines(petId).collect { vaccines ->
                    _uiState.update { it.copy(vaccines = vaccines) }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e("VaccineViewModel", "Failed to observe vaccines", exception)
                _uiState.update {
                    it.copy(errorMessage = "Não foi possível carregar a carteirinha.")
                }
            }
        }
    }

    fun onEvent(event: VaccineEvent) {
        when (event) {
            is VaccineEvent.NameChanged -> updateState { copy(name = event.value) }
            is VaccineEvent.AdministeredAtChanged ->
                updateState { copy(administeredAt = event.value.filter(Char::isDigit).take(8)) }
            is VaccineEvent.NextDueDateChanged ->
                updateState { copy(nextDueDate = event.value.filter(Char::isDigit).take(8)) }
            is VaccineEvent.VeterinarianChanged -> updateState { copy(veterinarian = event.value) }
            is VaccineEvent.NotesChanged -> updateState { copy(notes = event.value) }
            VaccineEvent.Save -> save()
        }
    }

    private fun updateState(update: VaccineUiState.() -> VaccineUiState) {
        _uiState.update { it.update().copy(errorMessage = null, successMessage = null) }
    }

    private fun save() {
        val state = _uiState.value
        if (state.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
            val vaccine = Vaccine(
                id = UUID.randomUUID().toString(),
                petId = petId,
                name = state.name.trim(),
                administeredAt = state.administeredAt.toDateDisplayFormat(),
                nextDueDate = state.nextDueDate.toDateDisplayFormat().ifBlank { null },
                veterinarian = state.veterinarian.trim().ifBlank { null },
                notes = state.notes.trim().ifBlank { null }
            )
            try {
                saveVaccine(vaccine)
                _uiState.update {
                    it.copy(
                        name = "",
                        administeredAt = "",
                        nextDueDate = "",
                        veterinarian = "",
                        notes = "",
                        isSaving = false,
                        successMessage = "Vacina salva na carteirinha."
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: IllegalArgumentException) {
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = exception.message)
                }
            } catch (exception: Exception) {
                Log.e("VaccineViewModel", "Failed to save vaccine", exception)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Não foi possível salvar a vacina. Tente novamente."
                    )
                }
            }
        }
    }

    class Factory(
        private val petId: String,
        private val observeVaccines: ObserveVaccinesUseCase,
        private val saveVaccine: SaveVaccineUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (!modelClass.isAssignableFrom(VaccineViewModel::class.java)) {
                throw IllegalArgumentException("ViewModel não suportada: ${modelClass.name}")
            }
            return VaccineViewModel(petId, observeVaccines, saveVaccine) as T
        }
    }
}
