package com.leandro.petpass.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.leandro.petpass.data.local.entity.VaccineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccineDao {
    @Query("SELECT * FROM vaccines WHERE petId = :petId ORDER BY createdAt DESC")
    fun observeVaccines(petId: String): Flow<List<VaccineEntity>>

    @Query("SELECT * FROM vaccines ORDER BY nextDueDate ASC")
    fun observeAllVaccines(): Flow<List<VaccineEntity>>

    @Query("UPDATE vaccines SET nextDueDate = NULL WHERE id = :vaccineId")
    suspend fun clearNextDueDate(vaccineId: String)

    @Insert
    suspend fun insertVaccine(vaccine: VaccineEntity)
}
