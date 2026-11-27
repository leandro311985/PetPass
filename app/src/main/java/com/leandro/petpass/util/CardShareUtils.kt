package com.leandro.petpass.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.leandro.petpass.presentation.rgCard.PetRgUiModel
import java.io.File
import java.io.FileOutputStream

object CardShareUtils {

    /**
     * Compartilha as informações do RG do Pet como texto simples (com link de emergência).
     */
    fun sharePetInfoText(context: Context, pet: PetRgUiModel) {
        val shareText = """
            🐾 REGISTRO GERAL ANIMAL (PETPASS) 🐾
            
            Nome: ${pet.name}
            Espécie: ${pet.species} | Sexo: ${pet.gender}
            Raça: ${pet.breed}
            Nascimento: ${pet.birthDate}
            Tutor: ${pet.ownerName}
            Contato de Emergência: ${pet.emergencyPhone}
            RGA: ${pet.rga.ifBlank { "Não informado" }}
            Microchip: ${pet.microchip.ifBlank { "Não possui" }}
            
            🔗 Perfil Completo & Carteira de Vacinas:
            ${pet.qrCodeUrl ?: "https://petpass.app/pet/${pet.id}"}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "RG Pet - ${pet.name}")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        context.startActivity(Intent.createChooser(intent, "Compartilhar RG de ${pet.name}"))
    }

    /**
     * Salva um Bitmap temporário e abre o Intent para compartilhar como imagem.
     */
    fun shareBitmapImage(context: Context, bitmap: Bitmap, title: String = "rg_pet") {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()

            val file = File(cachePath, "${title}_${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Compartilhar Imagem do RG"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
