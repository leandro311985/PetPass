package com.leandro.petpass.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class PetAgeUtilsTest {

    @Test
    fun calculateAgeAndLifeStage_puppy() {
        // Data de nascimento há 6 meses
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MONTH, -6)
        }
        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(calendar.time)

        val (age, stage) = PetAgeUtils.calculateAgeAndLifeStage(dateStr)
        assertEquals("6 meses", age)
        assertEquals("Filhote 🍼", stage)
    }

    @Test
    fun calculateAgeAndLifeStage_senior() {
        // Data de nascimento há 9 anos
        val calendar = Calendar.getInstance().apply {
            add(Calendar.YEAR, -9)
        }
        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(calendar.time)

        val (age, stage) = PetAgeUtils.calculateAgeAndLifeStage(dateStr)
        assertEquals("9 anos", age)
        assertEquals("Sênior 👑", stage)
    }

    @Test
    fun calculateAgeAndLifeStage_blankDate() {
        val (age, stage) = PetAgeUtils.calculateAgeAndLifeStage(" ")
        assertEquals("Não informada", age)
        assertEquals("Adulto ⭐", stage)
    }

    @Test
    fun calculateAgeAndLifeStage_adult() {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.YEAR, -3)
        }
        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(calendar.time)

        val (age, stage) = PetAgeUtils.calculateAgeAndLifeStage(dateStr)
        assertEquals("3 anos", age)
        assertEquals("Adulto ⭐", stage)
    }

    @Test
    fun calculateAgeAndLifeStage_invalidDate() {
        val (age, stage) = PetAgeUtils.calculateAgeAndLifeStage("data-invalida")
        assertEquals("data-invalida", age)
        assertEquals("Adulto ⭐", stage)
    }
}
