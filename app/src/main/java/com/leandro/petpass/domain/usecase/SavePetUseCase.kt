package com.leandro.petpass.domain.usecase

import com.leandro.petpass.domain.model.Pet
import com.leandro.petpass.domain.repository.PetRepository
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale

class SavePetUseCase(private val petRepository: PetRepository) {
    suspend operator fun invoke(pet: Pet, photoUri: String?) {
        require(pet.name.isNotBlank()) { "Informe o nome do pet." }
        require(pet.species.isNotBlank()) { "Informe a espécie do pet." }
        require(isValidBirthDate(pet.birthDate)) {
            "Informe uma data de nascimento válida no formato dd/mm/aaaa."
        }
        require(pet.gender.isNotBlank()) { "Informe o sexo do pet." }
        require(pet.ownerName.isNotBlank()) { "Informe o nome do tutor." }
        require(pet.emergencyPhone.isNotBlank()) { "Informe um telefone para contato." }

        petRepository.savePet(pet, photoUri)
    }

    private fun isValidBirthDate(value: String): Boolean {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).apply {
            isLenient = false
        }
        val position = ParsePosition(0)
        val parsedDate = formatter.parse(value, position)
        return parsedDate != null && position.index == value.length
    }
}
