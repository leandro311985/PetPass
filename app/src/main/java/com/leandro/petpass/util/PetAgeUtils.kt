package com.leandro.petpass.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object PetAgeUtils {
    fun calculateAgeAndLifeStage(birthDateStr: String): Pair<String, String> {
        if (birthDateStr.isBlank()) return Pair("Não informada", "Adulto ⭐")
        try {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).apply {
                isLenient = false
            }
            val date = formatter.parse(birthDateStr) ?: return Pair(birthDateStr, "Adulto ⭐")
            val dob = Calendar.getInstance().apply { time = date }
            val today = Calendar.getInstance()

            var years = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
            var months = today.get(Calendar.MONTH) - dob.get(Calendar.MONTH)
            if (today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
                months--
            }
            if (months < 0) {
                years--
                months += 12
            }

            val ageText = when {
                years > 0 -> if (months > 0) "$years a e $months m" else "$years anos"
                months > 0 -> "$months meses"
                else -> "Recém-nascido"
            }

            val totalMonths = years * 12 + months
            val lifeStage = when {
                totalMonths < 12 -> "Filhote 🍼"
                totalMonths < 84 -> "Adulto ⭐"
                else -> "Sênior 👑"
            }

            return Pair(ageText, lifeStage)
        } catch (_: Exception) {
            return Pair(birthDateStr, "Adulto ⭐")
        }
    }
}
