package com.leandro.petpass.presentation.addpet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.leandro.petpass.domain.model.Pet
import com.leandro.petpass.domain.usecase.SavePetUseCase
import java.util.Locale
import com.leandro.petpass.presentation.common.toDateDisplayFormat
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddPetViewModel(
    private val savePetUseCase: SavePetUseCase,
    initialPet: Pet? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        if (initialPet != null) {
            AddPetUiState(
                isEditing = true,
                name = initialPet.name,
                species = initialPet.species,
                breed = initialPet.breed,
                birthDate = initialPet.birthDate.filter(Char::isDigit),
                gender = initialPet.gender,
                rga = initialPet.rga,
                microchip = initialPet.microchip,
                ownerName = initialPet.ownerName,
                emergencyPhone = initialPet.emergencyPhone,
                photoUri = initialPet.photoPath
            )
        } else {
            AddPetUiState()
        }
    )
    val uiState = _uiState.asStateFlow()
    private val petId: String = initialPet?.id ?: UUID.randomUUID().toString()
    private val existingPhotoPath: String? = initialPet?.photoPath

    fun onEvent(event: AddPetEvent) {
        when (event) {
            is AddPetEvent.NameChanged -> updateState {
                copy(name = event.value.replaceFirstChar { character ->
                    if (character.isLowerCase()) character.titlecase(Locale("pt", "BR"))
                    else character.toString()
                })
            }
            is AddPetEvent.SpeciesChanged -> updateState { copy(species = event.value) }
            is AddPetEvent.BreedChanged -> updateState { copy(breed = event.value) }
            is AddPetEvent.BirthDateChanged -> updateState { copy(birthDate = event.value) }
            is AddPetEvent.GenderChanged -> updateState { copy(gender = event.value) }
            is AddPetEvent.RgaChanged -> updateState { copy(rga = event.value) }
            is AddPetEvent.MicrochipChanged -> updateState { copy(microchip = event.value) }
            is AddPetEvent.OwnerNameChanged -> updateState { copy(ownerName = event.value) }
            is AddPetEvent.EmergencyPhoneChanged ->
                updateState { copy(emergencyPhone = event.value) }
            is AddPetEvent.PhotoSelected -> updateState { copy(photoUri = event.uri) }
            AddPetEvent.Save -> savePet()
        }
    }

    private fun updateState(update: AddPetUiState.() -> AddPetUiState) {
        _uiState.update { it.update().copy(errorMessage = null) }
    }

    private fun savePet() {
        val state = _uiState.value
        if (state.isSaving || state.isSaved) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val photoUriToSave = if (state.photoUri == existingPhotoPath) null else state.photoUri
            val pet = Pet(
                id = petId,
                name = state.name.trim(),
                species = state.species.trim(),
                breed = state.breed.trim(),
                birthDate = state.birthDate.toDateDisplayFormat(),
                gender = state.gender.trim(),
                rga = state.rga.trim(),
                microchip = state.microchip.trim(),
                ownerName = state.ownerName.trim(),
                emergencyPhone = state.emergencyPhone.trim(),
                photoPath = existingPhotoPath
            )
            try {
                savePetUseCase(pet, photoUriToSave)
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: IllegalArgumentException) {
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = exception.message)
                }
            } catch (exception: Exception) {
                Log.e("AddPetViewModel", "Failed to save pet", exception)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Não foi possível salvar o pet. Tente novamente."
                    )
                }
            }
        }
    }

    class Factory(
        private val savePetUseCase: SavePetUseCase,
        private val initialPet: Pet? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (!modelClass.isAssignableFrom(AddPetViewModel::class.java)) {
                throw IllegalArgumentException("ViewModel não suportada: ${modelClass.name}")
            }
            return AddPetViewModel(savePetUseCase, initialPet) as T
        }
    }
}
