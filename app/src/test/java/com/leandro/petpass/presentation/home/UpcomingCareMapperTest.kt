package com.leandro.petpass.presentation.home

import com.leandro.petpass.domain.model.ExamRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpcomingCareMapperTest {

    @Test
    fun futureExamDate_isShownAsUpcomingCare() {
        val futureDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }.time
        val dateText = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))
            .format(futureDate)
        val exam = ExamRecord(
            id = "exam-1",
            petId = "pet-1",
            name = "Hemograma",
            performedAt = dateText,
            veterinarian = null,
            result = null
        )

        val reminders = buildUpcomingReminders("pet-1", emptyList(), listOf(exam))

        assertEquals(1, reminders.size)
        assertEquals("Hemograma", reminders.single().title)
        assertEquals(dateText, reminders.single().dueDate)
        assertEquals("Exame", reminders.single().category)
        assertTrue(!reminders.single().isOverdue)
    }

    @Test
    fun dismissedFutureExam_isNotShownAsUpcomingCare() {
        val futureDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }.time
        val dateText = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))
            .format(futureDate)
        val exam = ExamRecord(
            id = "exam-1",
            petId = "pet-1",
            name = "Hemograma",
            performedAt = dateText,
            veterinarian = null,
            result = null,
            reminderDismissed = true
        )

        val reminders = buildUpcomingReminders("pet-1", emptyList(), listOf(exam))

        assertTrue(reminders.isEmpty())
    }
}
