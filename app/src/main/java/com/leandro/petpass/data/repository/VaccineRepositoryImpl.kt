package com.leandro.petpass.data.repository

import com.leandro.petpass.data.local.dao.VaccineDao
import com.leandro.petpass.data.local.mapper.toDomain
import com.leandro.petpass.data.local.mapper.toEntity
import com.leandro.petpass.domain.model.Vaccine
import com.leandro.petpass.domain.repository.VaccineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class VaccineRepositoryImpl(
    private val vaccineDao: VaccineDao
) : VaccineRepository {
    override fun observeVaccines(petId: String): Flow<List<Vaccine>> =
        vaccineDao.observeVaccines(petId).map { vaccines ->
            vaccines.map { it.toDomain() }
        }

    override fun observeAllVaccines(): Flow<List<Vaccine>> =
        vaccineDao.observeAllVaccines().map { vaccines ->
            vaccines.map { it.toDomain() }
        }

    override suspend fun saveVaccine(vaccine: Vaccine): Unit =
        withContext(Dispatchers.IO) {
            vaccineDao.insertVaccine(vaccine.toEntity())
        }

    override suspend fun clearNextDueDate(vaccineId: String) =
        withContext(Dispatchers.IO) {
            vaccineDao.clearNextDueDate(vaccineId)
        }
}
