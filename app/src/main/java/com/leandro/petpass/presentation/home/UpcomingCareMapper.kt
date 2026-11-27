package com.leandro.petpass.presentation.home

import com.leandro.petpass.domain.model.ExamRecord
import com.leandro.petpass.domain.model.Vaccine
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal fun buildUpcomingReminders(
    petId: String,
    vaccines: List<Vaccine>,
    exams: List<ExamRecord>
): List<HealthReminderUiModel> {
    val today = Calendar.getInstance().startOfDay().timeInMillis
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).apply {
        isLenient = false
    }

    fun dateMillis(value: String?): Long? {
        if (value.isNullOrBlank()) return null
        val position = ParsePosition(0)
        val date = formatter.parse(value, position) ?: return null
        if (position.index != value.length) return null
        return Calendar.getInstance().apply {
            time = date
            startOfDay()
        }.timeInMillis
    }

    val reminders = mutableListOf<HealthReminderUiModel>()

    vaccines.asSequence()
        .filter { it.petId == petId }
        .forEach { vaccine ->
            val dueDate = vaccine.nextDueDate
            val dueMillis = dateMillis(dueDate)
            if (dueDate != null && dueMillis != null) {
                reminders += HealthReminderUiModel(
                    id = "vaccine:${vaccine.id}",
                    title = vaccine.name,
                    dueDate = dueDate,
                    category = "Vacina",
                    isOverdue = dueMillis < today
                )
            }
        }

    exams.asSequence()
        .filter { it.petId == petId && !it.reminderDismissed }
        .forEach { exam ->
            val nextDueDateMillis = dateMillis(exam.nextDueDate)
            val performedAtMillis = dateMillis(exam.performedAt)
            val dueDate = when {
                exam.nextDueDate != null && nextDueDateMillis != null -> exam.nextDueDate
                performedAtMillis != null && performedAtMillis > today -> exam.performedAt
                else -> null
            }
            val dueMillis = dateMillis(dueDate)
            if (dueDate != null && dueMillis != null) {
                reminders += HealthReminderUiModel(
                    id = "exam:${exam.id}",
                    title = exam.name,
                    dueDate = dueDate,
                    category = "Exame",
                    isOverdue = dueMillis < today
                )
            }
        }

    return reminders.sortedBy { dateMillis(it.dueDate) ?: Long.MAX_VALUE }
}

private fun Calendar.startOfDay(): Calendar = apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}
