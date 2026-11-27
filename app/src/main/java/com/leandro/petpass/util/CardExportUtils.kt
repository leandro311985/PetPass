package com.leandro.petpass.util

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import com.leandro.petpass.presentation.rgCard.PetRgUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object CardExportUtils {

    fun generateCardBitmap(pet: PetRgUiModel, isBack: Boolean = false): Bitmap {
        val width = 1200
        val height = 758 // Proporção 1.586
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint().apply { isAntiAlias = true }
        paint.color = Color.parseColor("#E0F2F1")
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rect, 32f, 32f, paint)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        paint.color = Color.parseColor("#00796B")
        canvas.drawRoundRect(rect, 32f, 32f, paint)
        paint.style = Paint.Style.FILL

        if (!isBack) {
            // --- FRENTE DO RG ---
            paint.color = Color.parseColor("#004D40")
            paint.textSize = 36f
            paint.isFakeBoldText = true
            canvas.drawText("🐾 REGISTRO GERAL ANIMAL (PETPASS)", 60f, 90f, paint)

            paint.color = Color.parseColor("#00796B")
            paint.strokeWidth = 3f
            canvas.drawLine(60f, 120f, (width - 60).toFloat(), 120f, paint)

            val photoRect = RectF(60f, 160f, 320f, 480f)
            paint.color = Color.WHITE
            canvas.drawRoundRect(photoRect, 24f, 24f, paint)
            paint.style = Paint.Style.STROKE
            paint.color = Color.parseColor("#00796B")
            paint.strokeWidth = 4f
            canvas.drawRoundRect(photoRect, 24f, 24f, paint)
            paint.style = Paint.Style.FILL

            try {
                if (!pet.photoUrl.isNullOrBlank()) {
                    val uri = android.net.Uri.parse(pet.photoUrl)
                    val file = when (uri.scheme) {
                        "file" -> File(uri.path ?: "")
                        else -> File(pet.photoUrl)
                    }
                    if (file.exists()) {
                        val photoBmp = BitmapFactory.decodeFile(file.absolutePath)
                        if (photoBmp != null) {
                            canvas.drawBitmap(photoBmp, null, photoRect, paint)
                            photoBmp.recycle()
                        }
                    }
                }
            } catch (_: Exception) {}

            val startX = 380f
            var startY = 210f

            fun drawField(label: String, value: String, x: Float, y: Float) {
                paint.color = Color.parseColor("#00695C")
                paint.textSize = 24f
                paint.isFakeBoldText = true
                canvas.drawText(label, x, y, paint)

                paint.color = Color.parseColor("#00251A")
                paint.textSize = 38f
                paint.isFakeBoldText = true
                canvas.drawText(value.uppercase(), x, y + 42f, paint)
            }

            drawField("NOME", pet.name, startX, startY)
            startY += 110f
            drawField("ESPÉCIE / SEXO", "${pet.species} • ${pet.gender}", startX, startY)
            startY += 110f
            drawField("RAÇA", pet.breed.ifBlank { "Não informada" }, startX, startY)
            startY += 110f

            val (ageText, lifeStage) = PetAgeUtils.calculateAgeAndLifeStage(pet.birthDate)
            drawField("IDADE / FASE", "$ageText ($lifeStage)", startX, startY)

            paint.color = Color.parseColor("#004D40")
            paint.textSize = 22f
            paint.isFakeBoldText = false
            canvas.drawText("VÁLIDO EM TODO TERRITÓRIO NACIONAL • ID #${pet.id.take(8).uppercase()}", 60f, (height - 50).toFloat(), paint)

        } else {
            // --- VERSO DO RG ---
            paint.color = Color.parseColor("#004D40")
            paint.textSize = 36f
            paint.isFakeBoldText = true
            canvas.drawText("DADOS DO TUTOR & EMERGÊNCIA", 60f, 90f, paint)

            paint.color = Color.parseColor("#00796B")
            paint.strokeWidth = 3f
            canvas.drawLine(60f, 120f, (width - 60).toFloat(), 120f, paint)

            var startY = 185f
            fun drawBackField(label: String, value: String) {
                paint.color = Color.parseColor("#00695C")
                paint.textSize = 22f
                paint.isFakeBoldText = true
                canvas.drawText(label, 60f, startY, paint)

                paint.color = Color.parseColor("#00251A")
                paint.textSize = 36f
                paint.isFakeBoldText = true
                canvas.drawText(value, 60f, startY + 40f, paint)
                startY += 105f
            }

            drawBackField("TUTOR RESPONSÁVEL", pet.ownerName)
            drawBackField("CONTATO DE EMERGÊNCIA", pet.emergencyPhone)
            drawBackField("RGA (REGISTRO GERAL)", pet.rga.ifBlank { "NÃO INFORMADO" })
            drawBackField("MICROCHIP", pet.microchip.ifBlank { "NÃO POSSUI" })

            val qrBitmap = QrCodeUtils.generateQrCodeBitmap(
                content = pet.qrCodeUrl ?: "https://petpass.app/pet/${pet.id}",
                width = 300,
                height = 300
            )
            if (qrBitmap != null) {
                val qrLeft = (width - 360).toFloat()
                val qrTop = 180f
                val qrRect = RectF(qrLeft, qrTop, qrLeft + 300f, qrTop + 300f)
                paint.color = Color.WHITE
                canvas.drawRoundRect(qrRect, 20f, 20f, paint)
                canvas.drawBitmap(qrBitmap, qrLeft + 20f, qrTop + 20f, paint)
            }
        }

        return bitmap
    }

    suspend fun shareBitmap(context: Context, bitmap: Bitmap, title: String) {
        withContext(Dispatchers.IO) {
            try {
                val cachePath = File(context.cacheDir, "images")
                cachePath.mkdirs()
                val file = File(cachePath, "${title}_${System.currentTimeMillis()}.png")
                FileOutputStream(file).use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }

                val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    clipData = ClipData.newUri(context.contentResolver, "Image", contentUri)
                }
                val chooser = Intent.createChooser(intent, "Compartilhar Imagem do RG").apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                withContext(Dispatchers.Main) {
                    try {
                        context.startActivity(chooser)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Erro ao abrir compartilhamento: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erro ao gerar imagem: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    suspend fun sharePdf(context: Context, pet: PetRgUiModel) {
        withContext(Dispatchers.IO) {
            try {
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(1200, 1600, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                val paint = Paint().apply { color = Color.WHITE }
                canvas.drawRect(0f, 0f, 1200f, 1600f, paint)

                paint.color = Color.parseColor("#004D40")
                paint.textSize = 44f
                paint.isFakeBoldText = true
                canvas.drawText("CARTEIRINHA DE IDENTIDADE ANIMAL", 80f, 100f, paint)

                val frontBmp = generateCardBitmap(pet, isBack = false)
                canvas.drawBitmap(frontBmp, 80f, 160f, null)
                frontBmp.recycle()

                val backBmp = generateCardBitmap(pet, isBack = true)
                canvas.drawBitmap(backBmp, 80f, 960f, null)
                backBmp.recycle()

                pdfDocument.finishPage(page)

                val cachePath = File(context.cacheDir, "pdf")
                cachePath.mkdirs()
                val file = File(cachePath, "RG_Pet_${pet.name}_${System.currentTimeMillis()}.pdf")
                FileOutputStream(file).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                pdfDocument.close()

                val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    clipData = ClipData.newUri(context.contentResolver, "PDF", contentUri)
                }
                val chooser = Intent.createChooser(intent, "Compartilhar PDF do RG").apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                withContext(Dispatchers.Main) {
                    try {
                        context.startActivity(chooser)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Erro ao abrir compartilhamento de PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erro ao gerar PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
