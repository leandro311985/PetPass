package com.leandro.petpass.presentation.common

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

fun String.toDateDisplayFormat(): String {
    val digits = filter(Char::isDigit).take(8)
    return buildString {
        append(digits.take(2))
        if (digits.length >= 2) append('/')
        if (digits.length > 2) append(digits.substring(2, minOf(4, digits.length)))
        if (digits.length >= 4) append('/')
        if (digits.length > 4) append(digits.substring(4))
    }
}

object DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter(Char::isDigit).take(8)
        val formatted = digits.toDateDisplayFormat()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clampedOffset = offset.coerceIn(0, digits.length)
                var transformedOffset = clampedOffset
                if (clampedOffset >= 2) transformedOffset++
                if (clampedOffset >= 4) transformedOffset++
                return transformedOffset.coerceIn(0, formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clampedOffset = offset.coerceIn(0, formatted.length)
                var originalOffset = clampedOffset
                if (clampedOffset > 2) originalOffset--
                if (clampedOffset > 5) originalOffset--
                return originalOffset.coerceIn(0, digits.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
