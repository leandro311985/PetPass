package com.leandro.petpass.domain.usecase

import com.leandro.petpass.domain.model.Pet
import com.leandro.petpass.domain.repository.PetRepository
import kotlinx.coroutines.flow.Flow

class ObservePetsUseCase(private val petRepository: PetRepository) {
    operator fun invoke(): Flow<List<Pet>> = petRepository.observePets()
}
