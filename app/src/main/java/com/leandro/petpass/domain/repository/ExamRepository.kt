package com.leandro.petpass.domain.repository

import com.leandro.petpass.domain.model.ExamRecord
import kotlinx.coroutines.flow.Flow

interface ExamRepository {
    fun observeExams(petId: String): Flow<List<ExamRecord>>

    fun observeLatestExamDates(): Flow<Map<String, String>>

    suspend fun saveExam(exam: ExamRecord)
}
