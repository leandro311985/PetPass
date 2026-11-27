package com.leandro.petpass.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leandro.petpass.presentation.rgCard.PetRgInteractiveCard
import com.leandro.petpass.presentation.rgCard.PetRgUiModel
import com.leandro.petpass.util.CardExportUtils
import com.leandro.petpass.util.CardShareUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetPassHomeScreen(
    pets: List<PetHomeUiModel>,
    selectedPetIndex: Int,
    onSelectPet: (Int) -> Unit,
    onAddPetClick: () -> Unit,
    onShareRgClick: (PetRgUiModel) -> Unit,
    onEditPetClick: (String) -> Unit,
    onNavigateToVaccines: (String) -> Unit,
    onNavigateToWeight: (String) -> Unit,
    onNavigateToExams: (String) -> Unit,
    onCompleteReminder: (String) -> Unit
) {
    val currentPet = pets.getOrNull(selectedPetIndex)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showShareDialog by remember { mutableStateOf(false) }

    if (showShareDialog && currentPet != null) {
        val pet = currentPet.rgData
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
                                val bitmap = CardExportUtils.generateCardBitmap(pet, isBack = false)
                                CardExportUtils.shareBitmap(context, bitmap, "RG_${pet.name}_Frente")
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
                                CardExportUtils.sharePdf(context, pet)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            tint = Color(0xFF00695C),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PetPass",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF004D40)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Abrir central de notificações */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificações"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (currentPet == null) {
            EmptyPetState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                onAddPetClick = onAddPetClick
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. Seletor Horizontal de Pets
                item {
                    PetSelectorRow(
                        pets = pets.map { it.rgData },
                        selectedIndex = selectedPetIndex,
                        onSelectPet = onSelectPet,
                        onAddPetClick = onAddPetClick
                    )
                }

                // 2. Card Interativo do RG + Ações Diretas
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        PetRgInteractiveCard(pet = currentPet.rgData)

                        // Alerta Médico (exibido apenas se o pet tiver restrição)
                        currentPet.medicalAlert?.let { alert ->
                            MedicalAlertBanner(alertText = alert)
                        }

                        // Botões Compartilhar / Editar RG
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    onShareRgClick(currentPet.rgData)
                                    showShareDialog = true
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00695C)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Compartilhar RG")
                            }

                            OutlinedButton(
                                onClick = { onEditPetClick(currentPet.rgData.id) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar Dados",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // 3. Resumo Rápido de Saúde (Peso e Última Consulta)
                item {
                    HealthSummarySection(
                        weightKg = currentPet.currentWeightKg,
                        lastVetVisit = currentPet.lastVetVisit,
                        onWeightClick = { onNavigateToWeight(currentPet.rgData.id) },
                        onVaccinesClick = { onNavigateToVaccines(currentPet.rgData.id) },
                        onExamsClick = { onNavigateToExams(currentPet.rgData.id) }
                    )
                }

                // 4. Próximos Lembretes (Vacinas / Vermífugos)
                item {
                    Text(
                        text = "Próximos Cuidados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00251A)
                    )
                }

                if (currentPet.upcomingReminders.isEmpty()) {
                    item {
                        Text(
                            text = "Tudo em dia! Nenhuma vacina ou medicação pendente.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(
                        items = currentPet.upcomingReminders,
                        key = { it.id }
                    ) { reminder ->
                        HealthReminderCard(
                            reminder = reminder,
                            onCompleteClick = { onCompleteReminder(reminder.id) }
                        )
                    }
                }
            }
        }
    }
}
