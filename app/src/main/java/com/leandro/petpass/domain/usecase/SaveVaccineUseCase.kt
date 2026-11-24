package com.leandro.petpass.domain.usecase

import com.leandro.petpass.domain.model.Vaccine
import com.leandro.petpass.domain.repository.VaccineRepository
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale

class SaveVaccineUseCase(private val repository: VaccineRepository) {
    suspend operator fun invoke(vaccine: Vaccine) {
        require(vaccine.name.isNotBlank()) { "Informe o nome da vacina." }
        require(isValidDate(vaccine.administeredAt)) {
            "Informe uma data de aplicação válida no formato dd/mm/aaaa."
        }
        require(vaccine.nextDueDate == null || isValidDate(vaccine.nextDueDate)) {
            "Informe uma data de reforço válida no formato dd/mm/aaaa."
        }
        repository.saveVaccine(vaccine)
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
