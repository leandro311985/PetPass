package com.leandro.petpass.domain.repository

import com.leandro.petpass.domain.model.Vaccine
import kotlinx.coroutines.flow.Flow

interface VaccineRepository {
    fun observeVaccines(petId: String): Flow<List<Vaccine>>

    fun observeAllVaccines(): Flow<List<Vaccine>>

    suspend fun saveVaccine(vaccine: Vaccine)

    suspend fun clearNextDueDate(vaccineId: String)
}
