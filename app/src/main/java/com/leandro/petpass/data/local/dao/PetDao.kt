package com.leandro.petpass.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.leandro.petpass.data.local.entity.PetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Query("SELECT * FROM pets ORDER BY createdAt DESC")
    fun observePets(): Flow<List<PetEntity>>

    @Upsert
    suspend fun upsertPet(pet: PetEntity)
}
