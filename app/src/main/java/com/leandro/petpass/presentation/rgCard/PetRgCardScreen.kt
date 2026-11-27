package com.leandro.petpass.presentation.rgCard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.leandro.petpass.util.CardShareUtils
import com.leandro.petpass.util.rememberQrCodeBitmap
import kotlinx.coroutines.launch


@Composable
fun PetRgCardScreen(
    pet: PetRgUiModel = samplePet,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showShareDialog by remember { mutableStateOf(false) }

    if (showShareDialog) {
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = { Text("Compartilhar RG do ${pet.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Escolha o formato de compartilhamento:")

                    OutlinedButton(
                        onClick = {
                            showShareDialog = false
                            CardShareUtils.sharePetInfoText(context, pet)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📄 Compartilhar Texto com Link")
                    }

                    OutlinedButton(
                        onClick = {
                            showShareDialog = false
                            scope.launch {
                                val bitmap = com.leandro.petpass.util.CardExportUtils.generateCardBitmap(pet, isBack = false)
                                com.leandro.petpass.util.CardExportUtils.shareBitmap(context, bitmap, "RG_${pet.name}_Frente")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🖼️ Compartilhar Imagem (Alta Resolução)")
                    }

                    OutlinedButton(
                        onClick = {
                            showShareDialog = false
                            scope.launch {
                                com.leandro.petpass.util.CardExportUtils.sharePdf(context, pet)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📑 Compartilhar PDF Oficial (Frente e Verso)")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showShareDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        PetRgInteractiveCard(
            pet = pet,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { showShareDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00695C)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Compartilhar RG")
            }
        }
    }
}

@Composable
fun PetRgInteractiveCard(
    pet: PetRgUiModel,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "rg_flip_animation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.586f) // Proporção padrão de cartão de identidade
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density // Evita distorção exagerada na rotação 3D
            }
            .clickable { isFlipped = !isFlipped },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        if (rotation <= 90f) {
            PetRgFront(pet = pet)
        } else {
            // Desvira o conteúdo do verso para não ficar espelhado
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                PetRgBack(pet = pet)
            }
        }
    }
}

@Composable
private fun PetRgFront(pet: PetRgUiModel) {
    val bgGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFE0F2F1), Color(0xFFB2DFDB))
    )

    val (ageText, lifeStage) = remember(pet.birthDate) {
        com.leandro.petpass.util.PetAgeUtils.calculateAgeAndLifeStage(pet.birthDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Cabeçalho do RG
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = Color(0xFF00695C),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "REGISTRO GERAL ANIMAL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF004D40)
                )
            }
            Text(
                text = "Toque para virar ↻",
                fontSize = 10.sp,
                color = Color(0xFF00695C)
            )
        }

        // Corpo: Foto (Coil) + Dados Principais
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto do Pet via Coil com corte arredondado e fallback
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(2.dp, Color(0xFF00796B), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!pet.photoUrl.isNullOrBlank()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(pet.photoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto de ${pet.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFF00796B),
                                strokeWidth = 2.dp
                            )
                        },
                        error = {
                            PhotoPlaceholderIcon()
                        }
                    )
                } else {
                    PhotoPlaceholderIcon()
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                RgField(label = "NOME", value = pet.name.uppercase())
                Spacer(modifier = Modifier.width(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    RgField(label = "ESPÉCIE", value = pet.species)
                    RgField(label = "SEXO", value = pet.gender)
                }
                Spacer(modifier = Modifier.width(2.dp))
                RgField(label = "RAÇA", value = pet.breed)
                Spacer(modifier = Modifier.width(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    RgField(label = "IDADE", value = ageText)
                    RgField(label = "FASE", value = lifeStage)
                }
            }
        }

        // Rodapé decorativo
        Text(
            text = "VÁLIDO EM TODO TERRITÓRIO NACIONAL • ID #${pet.id.take(6).uppercase()}",
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF004D40).copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun PhotoPlaceholderIcon() {
    Icon(
        imageVector = Icons.Default.Pets,
        contentDescription = "Foto do Pet",
        tint = Color(0xFF80CBC4),
        modifier = Modifier.size(40.dp)
    )
}

@Composable
private fun PetRgBack(pet: PetRgUiModel) {
    val bgGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFB2DFDB), Color(0xFFE0F2F1))
    )

    val targetUrl = pet.qrCodeUrl ?: "https://petpass.app/pet/${pet.id}"
    val qrCodeBitmap = rememberQrCodeBitmap(content = targetUrl, width = 512, height = 512)

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Informações de Filiação e Microchip
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "DADOS DO TUTOR & EMERGÊNCIA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF004D40)
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                RgField(label = "TUTOR RESPONSÁVEL", value = pet.ownerName)
                RgField(label = "CONTATO DE EMERGÊNCIA", value = pet.emergencyPhone)
                RgField(
                    label = "RGA (REGISTRO GERAL)",
                    value = pet.rga.ifBlank { "NÃO INFORMADO" }
                )
                RgField(
                    label = "MICROCHIP",
                    value = pet.microchip.ifBlank { "NÃO POSSUI" }
                )
            }

            Text(
                text = "Escaneie o QR Code ao lado para ver alergias e vacinas.",
                fontSize = 9.sp,
                color = Color(0xFF004D40)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Área do QR Code (ZXing Bitmap real)
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (qrCodeBitmap != null) {
                Image(
                    bitmap = qrCodeBitmap.asImageBitmap(),
                    contentDescription = "QR Code de Emergência",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = "QR Code de Emergência",
                    tint = Color.Black,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun RgField(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 8.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00695C)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF00251A),
            maxLines = 1
        )
    }
}

val samplePet = PetRgUiModel(
    id = "123456789",
    name = "Buddy",
    species = "Cão",
    breed = "Golden Retriever",
    birthDate = "15/05/2021",
    gender = "Macho",
    rga = "123.456.789-SP",
    microchip = "985141002345678",
    ownerName = "Ana Maria Silva",
    emergencyPhone = "(11) 98765-4321",
    photoUrl = "https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=400&q=80",
    qrCodeUrl = "https://petpass.app/pet/123456789"
)

@Preview(showBackground = true)
@Composable
fun PetRgCardScreenPreview() {
    MaterialTheme {
        PetRgCardScreen(pet = samplePet)
    }
}
