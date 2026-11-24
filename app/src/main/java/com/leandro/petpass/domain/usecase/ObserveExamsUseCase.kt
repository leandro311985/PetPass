package com.leandro.petpass.domain.usecase

import com.leandro.petpass.domain.model.ExamRecord
import com.leandro.petpass.domain.repository.ExamRepository
import kotlinx.coroutines.flow.Flow

class ObserveExamsUseCase(private val repository: ExamRepository) {
    operator fun invoke(petId: String): Flow<List<ExamRecord>> =
        repository.observeExams(petId)
}
