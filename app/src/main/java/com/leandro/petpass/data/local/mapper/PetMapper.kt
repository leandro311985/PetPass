package com.leandro.petpass.data.local.mapper

import com.leandro.petpass.data.local.entity.PetEntity
import com.leandro.petpass.domain.model.Pet

fun PetEntity.toDomain(): Pet = Pet(
    id = id,
    name = name,
    species = species,
    breed = breed,
    birthDate = birthDate,
    gender = gender,
    microchip = microchip,
    ownerName = ownerName,
    emergencyPhone = emergencyPhone,
    photoPath = photoPath
)

fun Pet.toEntity(createdAt: Long): PetEntity = PetEntity(
    id = id,
    name = name,
    species = species,
    breed = breed,
    birthDate = birthDate,
    gender = gender,
    microchip = microchip,
    ownerName = ownerName,
    emergencyPhone = emergencyPhone,
    photoPath = photoPath,
    createdAt = createdAt
)
