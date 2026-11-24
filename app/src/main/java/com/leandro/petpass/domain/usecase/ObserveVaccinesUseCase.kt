package com.leandro.petpass.domain.usecase

import com.leandro.petpass.domain.model.Vaccine
import com.leandro.petpass.domain.repository.VaccineRepository
import kotlinx.coroutines.flow.Flow

class ObserveVaccinesUseCase(private val repository: VaccineRepository) {
    operator fun invoke(petId: String): Flow<List<Vaccine>> =
        repository.observeVaccines(petId)
}
