package com.leandro.petpass.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.leandro.petpass.data.local.entity.ExamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams WHERE petId = :petId ORDER BY performedAt DESC, createdAt DESC")
    fun observeExams(petId: String): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams ORDER BY performedAt DESC, createdAt DESC")
    fun observeAllExams(): Flow<List<ExamEntity>>

    @Insert
    suspend fun insertExam(exam: ExamEntity)
}
