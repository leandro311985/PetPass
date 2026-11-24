package com.leandro.petpass.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey val id: String,
    val name: String,
    val species: String,
    val breed: String,
    val birthDate: String,
    val gender: String,
    val microchip: String,
    val ownerName: String,
    val emergencyPhone: String,
    val photoPath: String?,
    val createdAt: Long
)
