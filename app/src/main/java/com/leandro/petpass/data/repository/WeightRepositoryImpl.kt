package com.leandro.petpass.data.repository

import com.leandro.petpass.data.local.dao.WeightDao
import com.leandro.petpass.data.local.mapper.toDomain
import com.leandro.petpass.data.local.mapper.toEntity
import com.leandro.petpass.domain.model.WeightRecord
import com.leandro.petpass.domain.repository.WeightRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WeightRepositoryImpl(
    private val weightDao: WeightDao
) : WeightRepository {
    override fun observeWeightRecords(petId: String): Flow<List<WeightRecord>> =
        weightDao.observeWeightRecords(petId).map { records ->
            records.map { it.toDomain() }
        }

    override fun observeLatestWeights(): Flow<Map<String, Double>> =
        weightDao.observeAllWeightRecords().map { records ->
            records.distinctBy { it.petId }.associate { it.petId to it.weightKg }
        }

    override suspend fun saveWeightRecord(record: WeightRecord): Unit =
        withContext(Dispatchers.IO) {
            weightDao.insertWeightRecord(record.toEntity())
        }
}
