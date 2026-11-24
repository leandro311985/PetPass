package com.leandro.petpass.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

object QrCodeUtils {

    /**
     * Gera um Bitmap do QR Code com base em um texto ou URL.
     */
    fun generateQrCodeBitmap(
        content: String,
        width: Int = 512,
        height: Int = 512,
        qrColor: Int = Color.BLACK,
        bgColor: Int = Color.WHITE
    ): Bitmap? {
        if (content.isBlank()) return null
        return try {
            val hints = mapOf(
                EncodeHintType.MARGIN to 1,
                EncodeHintType.CHARACTER_SET to "UTF-8"
            )
            val bitMatrix = QRCodeWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
            )
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) qrColor else bgColor)
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Composable
fun rememberQrCodeBitmap(
    content: String,
    width: Int = 512,
    height: Int = 512,
    qrColor: Int = Color.BLACK,
    bgColor: Int = Color.WHITE
): Bitmap? {
    return remember(content, width, height, qrColor, bgColor) {
        QrCodeUtils.generateQrCodeBitmap(
            content = content,
            width = width,
            height = height,
            qrColor = qrColor,
            bgColor = bgColor
        )
    }
}
