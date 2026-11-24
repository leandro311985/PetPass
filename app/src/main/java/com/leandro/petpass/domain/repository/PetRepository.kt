package com.leandro.petpass.domain.repository

import com.leandro.petpass.domain.model.Pet
import kotlinx.coroutines.flow.Flow

interface PetRepository {
    fun observePets(): Flow<List<Pet>>

    suspend fun savePet(pet: Pet, photoUri: String?)
}
