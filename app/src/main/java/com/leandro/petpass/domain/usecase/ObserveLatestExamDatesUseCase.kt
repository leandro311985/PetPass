package com.leandro.petpass.domain.usecase

import com.leandro.petpass.domain.repository.ExamRepository
import kotlinx.coroutines.flow.Flow

class ObserveLatestExamDatesUseCase(private val repository: ExamRepository) {
    operator fun invoke(): Flow<Map<String, String>> = repository.observeLatestExamDates()
}
