package com.leandro.petpass.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weights",
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("petId")]
)
data class WeightEntity(
    @PrimaryKey val id: String,
    val petId: String,
    val weightKg: Double,
    val measuredAt: String,
    val veterinarian: String?,
    val notes: String?,
    val createdAt: Long
)
