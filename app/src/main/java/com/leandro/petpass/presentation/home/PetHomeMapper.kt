package com.leandro.petpass.presentation.home

import android.net.Uri
import com.leandro.petpass.domain.model.ExamRecord
import com.leandro.petpass.domain.model.Pet
import com.leandro.petpass.domain.model.Vaccine
import com.leandro.petpass.presentation.rgCard.PetRgUiModel
import com.leandro.petpass.presentation.common.toDateDisplayFormat
import java.io.File

fun Pet.toHomeUiModel(
    currentWeightKg: Double? = null,
    lastVetVisit: String = "Sem consultas",
    vaccines: List<Vaccine> = emptyList(),
    exams: List<ExamRecord> = emptyList()
): PetHomeUiModel = PetHomeUiModel(
    rgData = PetRgUiModel(
        id = id,
        name = name,
        species = species,
        breed = breed.ifBlank { "Não informada" },
        birthDate = birthDate.toDateDisplayFormat(),
        gender = gender,
        rga = rga,
        microchip = microchip,
        ownerName = ownerName,
        emergencyPhone = emergencyPhone,
        photoUrl = photoPath?.let { Uri.fromFile(File(it)).toString() },
        qrCodeUrl = "https://petpass.app/pet/$id"
    ),
    currentWeightKg = currentWeightKg,
    lastVetVisit = lastVetVisit,
    medicalAlert = null,
    upcomingReminders = buildUpcomingReminders(id, vaccines, exams)
)
