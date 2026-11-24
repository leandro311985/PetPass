package com.leandro.petpass.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.leandro.petpass.data.local.entity.PetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Query("SELECT * FROM pets ORDER BY createdAt DESC")
    fun observePets(): Flow<List<PetEntity>>

    @Insert
    suspend fun insertPet(pet: PetEntity)
}
