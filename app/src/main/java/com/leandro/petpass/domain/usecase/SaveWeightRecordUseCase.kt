package com.leandro.petpass.domain.usecase

import com.leandro.petpass.domain.model.WeightRecord
import com.leandro.petpass.domain.repository.WeightRepository
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale

class SaveWeightRecordUseCase(private val repository: WeightRepository) {
    suspend operator fun invoke(record: WeightRecord) {
        require(record.weightKg.isFinite() && record.weightKg > 0) {
            "Informe um peso maior que zero."
        }
        require(isValidDate(record.measuredAt)) {
            "Informe uma data válida no formato dd/mm/aaaa."
        }
        repository.saveWeightRecord(record)
    }

    private fun isValidDate(value: String): Boolean {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).apply {
            isLenient = false
        }
        val position = ParsePosition(0)
        val parsed = formatter.parse(value, position)
        return parsed != null && position.index == value.length
    }
}
