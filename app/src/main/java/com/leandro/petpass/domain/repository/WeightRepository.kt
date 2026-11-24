package com.leandro.petpass.domain.repository

import com.leandro.petpass.domain.model.WeightRecord
import kotlinx.coroutines.flow.Flow

interface WeightRepository {
    fun observeWeightRecords(petId: String): Flow<List<WeightRecord>>

    fun observeLatestWeights(): Flow<Map<String, Double>>

    suspend fun saveWeightRecord(record: WeightRecord)
}
