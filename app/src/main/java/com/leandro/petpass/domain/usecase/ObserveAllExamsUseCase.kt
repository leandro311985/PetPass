package com.leandro.petpass.domain.usecase

import com.leandro.petpass.domain.model.ExamRecord
import com.leandro.petpass.domain.repository.ExamRepository
import kotlinx.coroutines.flow.Flow

class ObserveAllExamsUseCase(private val repository: ExamRepository) {
    operator fun invoke(): Flow<List<ExamRecord>> = repository.observeAllExams()
}
