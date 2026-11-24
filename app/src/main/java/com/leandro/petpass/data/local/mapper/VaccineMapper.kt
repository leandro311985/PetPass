package com.leandro.petpass.data.local.mapper

import com.leandro.petpass.data.local.entity.VaccineEntity
import com.leandro.petpass.domain.model.Vaccine

fun VaccineEntity.toDomain(): Vaccine = Vaccine(
    id = id,
    petId = petId,
    name = name,
    administeredAt = administeredAt,
    nextDueDate = nextDueDate,
    veterinarian = veterinarian,
    notes = notes
)

fun Vaccine.toEntity(): VaccineEntity = VaccineEntity(
    id = id,
    petId = petId,
    name = name,
    administeredAt = administeredAt,
    nextDueDate = nextDueDate,
    veterinarian = veterinarian,
    notes = notes,
    createdAt = System.currentTimeMillis()
)
