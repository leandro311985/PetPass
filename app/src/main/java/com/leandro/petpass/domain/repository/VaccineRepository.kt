package com.leandro.petpass.domain.repository

import com.leandro.petpass.domain.model.Vaccine
import kotlinx.coroutines.flow.Flow

interface VaccineRepository {
    fun observeVaccines(petId: String): Flow<List<Vaccine>>

    suspend fun saveVaccine(vaccine: Vaccine)
}
