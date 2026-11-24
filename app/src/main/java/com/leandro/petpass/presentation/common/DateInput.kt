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
        val digits = text.text
        val hasDaySeparator = digits.length >= 2
        val hasMonthSeparator = digits.length >= 4
        val formatted = digits.toDateDisplayFormat()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var transformedOffset = offset
                if (hasDaySeparator && offset >= 2) transformedOffset++
                if (hasMonthSeparator && offset >= 4) transformedOffset++
                return transformedOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                var originalOffset = offset
                if (hasDaySeparator && offset > 2) originalOffset--
                if (hasMonthSeparator && offset > 5) originalOffset--
                return originalOffset.coerceIn(0, digits.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
