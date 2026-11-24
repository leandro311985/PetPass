package com.leandro.petpass.domain.usecase

import com.leandro.petpass.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow

class ObserveLatestWeightsUseCase(private val repository: WeightRepository) {
    operator fun invoke(): Flow<Map<String, Double>> = repository.observeLatestWeights()
}
