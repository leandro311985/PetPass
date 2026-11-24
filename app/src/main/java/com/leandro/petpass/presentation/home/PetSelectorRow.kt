package com.leandro.petpass.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leandro.petpass.presentation.rgCard.PetRgUiModel

@Composable
fun PetSelectorRow(
    pets: List<PetRgUiModel>,
    selectedIndex: Int,
    onSelectPet: (Int) -> Unit,
    onAddPetClick: () -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(pets.size) { index ->
            val pet = pets[index]
            val isSelected = index == selectedIndex

            FilterChip(
                selected = isSelected,
                onClick = { onSelectPet(index) },
                label = {
                    Text(
                        text = pet.name,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                shape = CircleShape
            )
        }

        item {
            AssistChip(
                onClick = onAddPetClick,
                label = { Text("Novo Pet") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar Pet",
                        modifier = Modifier.size(16.dp)
                    )
                },
                shape = CircleShape
            )
        }
    }
}

@Composable
fun MedicalAlertBanner(alertText: String) {
    Surface(
        color = Color(0xFFFFEBEE),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFFEF9A9A)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFC62828),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = alertText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB71C1C)
            )
        }
    }
}

@Composable
fun HealthSummarySection(
    weightKg: Double?,
    lastVetVisit: String,
    onWeightClick: () -> Unit,
    onVaccinesClick: () -> Unit,
    onExamsClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Saúde & Prontuário",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00251A)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionTile(
                title = "Vacinas",
                subtitle = "Carteirinha",
                icon = Icons.Default.Vaccines,
                containerColor = Color(0xFFE0F2F1),
                contentColor = Color(0xFF00695C),
                modifier = Modifier.weight(1f),
                onClick = onVaccinesClick
            )
            QuickActionTile(
                title = weightKg?.let { "${formatWeight(it)} kg" } ?: "Adicionar",
                subtitle = if (weightKg == null) "Registrar peso" else "Peso atual",
                icon = Icons.Default.MonitorWeight,
                containerColor = Color(0xFFFFF3E0),
                contentColor = Color(0xFFE65100),
                modifier = Modifier.weight(1f),
                onClick = onWeightClick
            )
            QuickActionTile(
                title = "Exames",
                subtitle = lastVetVisit,
                icon = Icons.Default.MedicalServices,
                containerColor = Color(0xFFE3F2FD),
                contentColor = Color(0xFF1565C0),
                modifier = Modifier.weight(1f),
                onClick = onExamsClick
            )
        }
    }
}

private fun formatWeight(weightKg: Double): String =
    java.text.DecimalFormat("0.##", java.text.DecimalFormatSymbols(java.util.Locale("pt", "BR")))
        .format(weightKg)

@Composable
private fun QuickActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = containerColor,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.height(92.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = contentColor.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun HealthReminderCard(
    reminder: HealthReminderUiModel,
    onCompleteClick: () -> Unit
) {
    val badgeColor = if (reminder.isOverdue) Color(0xFFC62828) else Color(0xFF00695C)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(badgeColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = badgeColor
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = reminder.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${reminder.category} • ${reminder.dueDate}",
                        fontSize = 12.sp,
                        color = badgeColor
                    )
                }
            }

            IconButton(onClick = onCompleteClick) {
                Icon(
                    imageVector = Icons.Default.CheckCircleOutline,
                    contentDescription = "Marcar como concluído",
                    tint = Color(0xFF00695C)
                )
            }
        }
    }
}

@Composable
fun EmptyPetState(
    modifier: Modifier = Modifier,
    onAddPetClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Pets,
            contentDescription = null,
            tint = Color(0xFF80CBC4),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nenhum pet cadastrado ainda",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Crie o RG digital e acompanhe as vacinas do seu melhor amigo em um só lugar.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onAddPetClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C))
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cadastrar Primeiro Pet")
        }
    }
}
