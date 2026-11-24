package com.leandro.petpass.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vaccines",
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
data class VaccineEntity(
    @PrimaryKey val id: String,
    val petId: String,
    val name: String,
    val administeredAt: String,
    val nextDueDate: String?,
    val veterinarian: String?,
    val notes: String?,
    val createdAt: Long
)
