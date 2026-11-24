package com.leandro.petpass.domain.usecase

import com.leandro.petpass.domain.model.ExamRecord
import com.leandro.petpass.domain.repository.ExamRepository
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale

class SaveExamUseCase(private val repository: ExamRepository) {
    suspend operator fun invoke(exam: ExamRecord) {
        require(exam.name.isNotBlank()) { "Informe o nome do exame." }
        require(isValidDate(exam.performedAt)) {
            "Informe uma data válida no formato dd/mm/aaaa."
        }
        repository.saveExam(exam)
    }

    private fun isValidDate(value: String): Boolean {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).apply {
            isLenient = false
        }
        val position = ParsePosition(0)
        val parsed = formatter.parse(value, position)
        return parsed != null && position.index == value.length
    }
}
