package com.leandro.petpass.data.repository

import com.leandro.petpass.data.local.dao.ExamDao
import com.leandro.petpass.data.local.mapper.toDomain
import com.leandro.petpass.data.local.mapper.toEntity
import com.leandro.petpass.domain.model.ExamRecord
import com.leandro.petpass.domain.repository.ExamRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ExamRepositoryImpl(private val examDao: ExamDao) : ExamRepository {
    override fun observeExams(petId: String): Flow<List<ExamRecord>> =
        examDao.observeExams(petId).map { exams -> exams.map { it.toDomain() } }

    override fun observeLatestExamDates(): Flow<Map<String, String>> =
        examDao.observeAllExams().map { exams ->
            exams.distinctBy { it.petId }.associate { it.petId to it.toDomain().performedAt }
        }

    override suspend fun saveExam(exam: ExamRecord): Unit =
        withContext(Dispatchers.IO) {
            examDao.insertExam(exam.toEntity())
        }
}
