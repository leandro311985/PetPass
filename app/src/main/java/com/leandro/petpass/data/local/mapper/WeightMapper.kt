package com.leandro.petpass.data.local.mapper

import com.leandro.petpass.data.local.entity.WeightEntity
import com.leandro.petpass.domain.model.WeightRecord
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val storageDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply {
    isLenient = false
    timeZone = TimeZone.getTimeZone("UTC")
}
private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).apply {
    isLenient = false
    timeZone = TimeZone.getTimeZone("UTC")
}

fun WeightEntity.toDomain(): WeightRecord {
    val parsedDate = storageDateFormat.parse(measuredAt)
        ?: throw IllegalStateException("Invalid stored weight date: $measuredAt")
    return WeightRecord(
        id = id,
        petId = petId,
        weightKg = weightKg,
        measuredAt = displayDateFormat.format(parsedDate),
        veterinarian = veterinarian,
        notes = notes
    )
}

fun WeightRecord.toEntity(): WeightEntity {
    val parsedDate = displayDateFormat.parse(measuredAt)
        ?: throw IllegalArgumentException("Invalid weight date: $measuredAt")
    return WeightEntity(
        id = id,
        petId = petId,
        weightKg = weightKg,
        measuredAt = storageDateFormat.format(parsedDate),
        veterinarian = veterinarian,
        notes = notes,
        createdAt = System.currentTimeMillis()
    )
}
