package com.leandro.petpass.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(
    tableName = "exams",
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
data class ExamEntity(
    @PrimaryKey val id: String,
    val petId: String,
    val name: String,
    val performedAt: String,
    val veterinarian: String?,
    val result: String?,
    val nextDueDate: String?,
    @ColumnInfo(defaultValue = "0") val reminderDismissed: Boolean = false,
    val createdAt: Long
)
