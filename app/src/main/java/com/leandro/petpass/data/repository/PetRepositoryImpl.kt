package com.leandro.petpass.data.repository

import com.leandro.petpass.data.local.dao.PetDao
import com.leandro.petpass.data.local.mapper.toDomain
import com.leandro.petpass.data.local.mapper.toEntity
import com.leandro.petpass.data.local.storage.LocalImageStorage
import com.leandro.petpass.domain.model.Pet
import com.leandro.petpass.domain.repository.PetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PetRepositoryImpl(
    private val petDao: PetDao,
    private val imageStorage: LocalImageStorage
) : PetRepository {
    override fun observePets(): Flow<List<Pet>> =
        petDao.observePets().map { entities -> entities.map { it.toDomain() } }

    override suspend fun savePet(pet: Pet, photoUri: String?): Unit =
        withContext(Dispatchers.IO) {
        val photoPath = photoUri?.let(imageStorage::saveImage)
        try {
            petDao.insertPet(
                pet.copy(photoPath = photoPath).toEntity(System.currentTimeMillis())
            )
        } catch (exception: Exception) {
            photoPath?.let(imageStorage::deleteImage)
            throw exception
        }
    }
}
