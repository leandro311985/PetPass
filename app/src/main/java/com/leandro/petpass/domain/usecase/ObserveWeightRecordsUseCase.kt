package com.leandro.petpass.domain.usecase

import com.leandro.petpass.domain.model.WeightRecord
import com.leandro.petpass.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow

class ObserveWeightRecordsUseCase(private val repository: WeightRepository) {
    operator fun invoke(petId: String): Flow<List<WeightRecord>> =
        repository.observeWeightRecords(petId)
}
