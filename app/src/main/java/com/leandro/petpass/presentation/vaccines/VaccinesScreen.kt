package com.leandro.petpass.presentation.vaccines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leandro.petpass.domain.model.Vaccine
import com.leandro.petpass.presentation.common.DateVisualTransformation

private val PetPassMint = Color(0xFFE0F2F1)
private val PetPassGreen = Color(0xFF00695C)
private val PetPassGreenDark = Color(0xFF004D40)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinesScreen(
    petName: String,
    state: VaccineUiState,
    onEvent: (VaccineEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF5FAF9),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Carteirinha de vacinas",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PetPassMint,
                    titleContentColor = PetPassGreenDark,
                    navigationIconContentColor = PetPassGreen
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = petName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = PetPassGreenDark
                    )
                    Text(
                        text = "Registre as doses e acompanhe os próximos reforços.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PetPassMint
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = PetPassGreen
                            )
                            Text(
                                text = "Registrar vacina",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PetPassGreenDark,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = { onEvent(VaccineEvent.NameChanged(it)) },
                            label = { Text("Vacina *") },
                            placeholder = { Text("Ex.: V10, antirrábica") },
                            singleLine = true,
                            colors = petPassTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        DateField(
                            value = state.administeredAt,
                            label = "Data da aplicação *",
                            onValueChange = { onEvent(VaccineEvent.AdministeredAtChanged(it)) }
                        )
                        DateField(
                            value = state.nextDueDate,
                            label = "Próximo reforço (opcional)",
                            onValueChange = { onEvent(VaccineEvent.NextDueDateChanged(it)) }
                        )
                        OutlinedTextField(
                            value = state.veterinarian,
                            onValueChange = { onEvent(VaccineEvent.VeterinarianChanged(it)) },
                            label = { Text("Veterinário ou clínica (opcional)") },
                            singleLine = true,
                            colors = petPassTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.notes,
                            onValueChange = { onEvent(VaccineEvent.NotesChanged(it)) },
                            label = { Text("Observações (opcional)") },
                            minLines = 2,
                            colors = petPassTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        state.errorMessage?.let { message ->
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        state.successMessage?.let { message ->
                            Text(
                                text = message,
                                color = PetPassGreen,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Button(
                            onClick = { onEvent(VaccineEvent.Save) },
                            enabled = !state.isSaving,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PetPassGreen,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text("Salvando...", modifier = Modifier.padding(start = 8.dp))
                            } else {
                                Text("Salvar vacina")
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Histórico (${state.vaccines.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PetPassGreenDark
                )
            }

            if (state.vaccines.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = PetPassMint
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Vaccines,
                                contentDescription = null,
                                tint = PetPassGreen,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Nenhuma vacina registrada",
                                fontWeight = FontWeight.SemiBold,
                                color = PetPassGreenDark
                            )
                            Text(
                                text = "As vacinas adicionadas aparecerão aqui.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(state.vaccines, key = { it.id }) { vaccine ->
                    VaccineHistoryCard(vaccine)
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun DateField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.filter(Char::isDigit).take(8)) },
        label = { Text(label) },
        placeholder = { Text("dd/mm/aaaa") },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = PetPassGreen
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = DateVisualTransformation,
        colors = petPassTextFieldColors(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun petPassTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PetPassGreen,
    focusedLabelColor = PetPassGreen,
    cursorColor = PetPassGreen,
    focusedLeadingIconColor = PetPassGreen
)

@Composable
private fun VaccineHistoryCard(vaccine: Vaccine) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = PetPassMint
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Vaccines,
                    contentDescription = null,
                    tint = PetPassGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = vaccine.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PetPassGreenDark,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(
                text = "Aplicada em ${vaccine.administeredAt}",
                style = MaterialTheme.typography.bodyMedium
            )
            vaccine.nextDueDate?.let { dueDate ->
                Text(
                    text = "Próximo reforço: $dueDate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PetPassGreen
                )
            }
            vaccine.veterinarian?.let { veterinarian ->
                Text(
                    text = veterinarian,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            vaccine.notes?.let { notes ->
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview
@Composable
private fun VaccinesScreenPreview() {
    VaccinesScreen(
        petName = "Buddy"   ,
        state = VaccineUiState(),
        onEvent = {},
        onBack = {}
    )
}