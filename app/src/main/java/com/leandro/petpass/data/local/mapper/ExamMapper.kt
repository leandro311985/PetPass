package com.leandro.petpass.data.local.mapper

import com.leandro.petpass.data.local.entity.ExamEntity
import com.leandro.petpass.domain.model.ExamRecord
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val examStorageDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply {
    isLenient = false
    timeZone = TimeZone.getTimeZone("UTC")
}
private val examDisplayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).apply {
    isLenient = false
    timeZone = TimeZone.getTimeZone("UTC")
}

private fun parseExamDate(value: String, format: SimpleDateFormat) =
    format.parse(value, ParsePosition(0))

fun ExamEntity.toDomain(): ExamRecord {
    val date = parseExamDate(performedAt, examStorageDateFormat)
        ?: throw IllegalStateException("Invalid stored exam date: $performedAt")
    return ExamRecord(
        id = id,
        petId = petId,
        name = name,
        performedAt = examDisplayDateFormat.format(date),
        veterinarian = veterinarian,
        result = result
    )
}

fun ExamRecord.toEntity(): ExamEntity {
    val date = parseExamDate(performedAt, examDisplayDateFormat)
        ?: throw IllegalArgumentException("Invalid exam date: $performedAt")
    return ExamEntity(
        id = id,
        petId = petId,
        name = name,
        performedAt = examStorageDateFormat.format(date),
        veterinarian = veterinarian,
        result = result,
        createdAt = System.currentTimeMillis()
    )
}
