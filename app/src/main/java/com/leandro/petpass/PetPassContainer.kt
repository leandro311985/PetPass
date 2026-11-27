package com.leandro.petpass

import android.content.Context
import androidx.room.Room
import com.leandro.petpass.data.local.PetPassDatabase
import com.leandro.petpass.data.local.storage.LocalImageStorage
import com.leandro.petpass.data.repository.PetRepositoryImpl
import com.leandro.petpass.data.repository.ExamRepositoryImpl
import com.leandro.petpass.data.repository.VaccineRepositoryImpl
import com.leandro.petpass.data.repository.WeightRepositoryImpl
import com.leandro.petpass.domain.usecase.ObservePetsUseCase
import com.leandro.petpass.domain.usecase.ObserveExamsUseCase
import com.leandro.petpass.domain.usecase.ObserveLatestExamDatesUseCase
import com.leandro.petpass.domain.usecase.ObserveVaccinesUseCase
import com.leandro.petpass.domain.usecase.ObserveLatestWeightsUseCase
import com.leandro.petpass.domain.usecase.ObserveWeightRecordsUseCase
import com.leandro.petpass.domain.usecase.SavePetUseCase
import com.leandro.petpass.domain.usecase.SaveExamUseCase
import com.leandro.petpass.domain.usecase.SaveVaccineUseCase
import com.leandro.petpass.domain.usecase.SaveWeightRecordUseCase

class PetPassContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        PetPassDatabase::class.java,
        "petpass.db"
    ).addMigrations(
        PetPassDatabase.MIGRATION_1_2,
        PetPassDatabase.MIGRATION_2_3,
        PetPassDatabase.MIGRATION_3_4,
        PetPassDatabase.MIGRATION_4_5
    ).build()

    private val petRepository = PetRepositoryImpl(
        petDao = database.petDao(),
        imageStorage = LocalImageStorage(context)
    )

    val observePetsUseCase = ObservePetsUseCase(petRepository)
    val savePetUseCase = SavePetUseCase(petRepository)
    private val vaccineRepository = VaccineRepositoryImpl(database.vaccineDao())
    val observeVaccinesUseCase = ObserveVaccinesUseCase(vaccineRepository)
    val saveVaccineUseCase = SaveVaccineUseCase(vaccineRepository)
    private val weightRepository = WeightRepositoryImpl(database.weightDao())
    val observeWeightRecordsUseCase = ObserveWeightRecordsUseCase(weightRepository)
    val observeLatestWeightsUseCase = ObserveLatestWeightsUseCase(weightRepository)
    val saveWeightRecordUseCase = SaveWeightRecordUseCase(weightRepository)
    private val examRepository = ExamRepositoryImpl(database.examDao())
    val observeExamsUseCase = ObserveExamsUseCase(examRepository)
    val observeLatestExamDatesUseCase = ObserveLatestExamDatesUseCase(examRepository)
    val saveExamUseCase = SaveExamUseCase(examRepository)
}
