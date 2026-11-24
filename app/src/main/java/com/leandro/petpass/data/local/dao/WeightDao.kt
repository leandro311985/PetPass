package com.leandro.petpass.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.leandro.petpass.data.local.entity.WeightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Query("SELECT * FROM weights WHERE petId = :petId ORDER BY measuredAt DESC, createdAt DESC")
    fun observeWeightRecords(petId: String): Flow<List<WeightEntity>>

    @Query("SELECT * FROM weights ORDER BY measuredAt DESC, createdAt DESC")
    fun observeAllWeightRecords(): Flow<List<WeightEntity>>

    @Insert
    suspend fun insertWeightRecord(record: WeightEntity)
}
