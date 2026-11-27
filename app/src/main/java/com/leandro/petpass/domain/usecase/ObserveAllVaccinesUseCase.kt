package com.leandro.petpass.domain.usecase

import com.leandro.petpass.domain.model.Vaccine
import com.leandro.petpass.domain.repository.VaccineRepository
import kotlinx.coroutines.flow.Flow

class ObserveAllVaccinesUseCase(private val repository: VaccineRepository) {
    operator fun invoke(): Flow<List<Vaccine>> = repository.observeAllVaccines()
}
