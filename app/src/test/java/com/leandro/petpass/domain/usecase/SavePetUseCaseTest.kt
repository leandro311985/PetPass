package com.leandro.petpass.domain.usecase

import com.leandro.petpass.domain.model.Pet
import com.leandro.petpass.domain.repository.PetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SavePetUseCaseTest {

    private val fakeRepository = object : PetRepository {
        var savedPet: Pet? = null
        override fun observePets(): Flow<List<Pet>> = flowOf(emptyList())
        override suspend fun savePet(pet: Pet, photoUri: String?) {
            savedPet = pet
        }
    }

    private val savePetUseCase = SavePetUseCase(fakeRepository)

    @Test
    fun invoke_validPet_savesSuccessfully() {
        val pet = Pet(
            id = "1",
            name = "Rex",
            species = "Cão",
            breed = "Labrador",
            birthDate = "10/10/2020",
            gender = "Macho",
            rga = "12345",
            microchip = "98765",
            ownerName = "Ana",
            emergencyPhone = "11999999999",
            photoPath = null
        )

        runBlocking {
            savePetUseCase(pet, null)
        }
        assertEquals(fakeRepository.savedPet, pet)
    }

    @Test
    fun invoke_blankName_throwsIllegalArgumentException() {
        val pet = Pet(
            id = "1",
            name = "",
            species = "Cão",
            breed = "Labrador",
            birthDate = "10/10/2020",
            gender = "Macho",
            rga = "12345",
            microchip = "98765",
            ownerName = "Ana",
            emergencyPhone = "11999999999",
            photoPath = null
        )

        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                savePetUseCase(pet, null)
            }
        }
    }

    @Test
    fun invoke_invalidBirthDate_throwsIllegalArgumentException() {
        val pet = Pet(
            id = "1",
            name = "Rex",
            species = "Cão",
            breed = "Labrador",
            birthDate = "35/13/2020", // Data inválida
            gender = "Macho",
            rga = "12345",
            microchip = "98765",
            ownerName = "Ana",
            emergencyPhone = "11999999999",
            photoPath = null
        )

        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                savePetUseCase(pet, null)
            }
        }
    }
}
