package com.leandro.petpass.data.local.storage

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.IOException
import java.util.UUID

class LocalImageStorage(context: Context) {
    private val contentResolver = context.applicationContext.contentResolver
    private val imageDirectory = File(context.applicationContext.filesDir, "pet-images")

    fun saveImage(sourceUri: String): String {
        if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
            throw IOException("Não foi possível criar o diretório de fotos.")
        }

        val destination = File(imageDirectory, "pet_${UUID.randomUUID()}")
        try {
            val input = contentResolver.openInputStream(Uri.parse(sourceUri))
                ?: throw IOException("Não foi possível abrir a foto selecionada.")
            input.use { stream ->
                destination.outputStream().use(stream::copyTo)
            }
        } catch (exception: Exception) {
            destination.delete()
            throw exception
        }
        return destination.absolutePath
    }

    fun deleteImage(path: String) {
        val image = File(path)
        if (image.parentFile?.canonicalFile == imageDirectory.canonicalFile) {
            image.delete()
        }
    }
}
