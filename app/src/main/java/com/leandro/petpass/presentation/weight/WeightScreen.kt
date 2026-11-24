package com.leandro.petpass.presentation.weight

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
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.leandro.petpass.domain.model.WeightRecord
import com.leandro.petpass.presentation.common.DateVisualTransformation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val PetPassMint = Color(0xFFE0F2F1)
private val PetPassGreen = Color(0xFF00695C)
private val PetPassGreenDark = Color(0xFF004D40)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(
    petName: String,
    state: WeightUiState,
    onEvent: (WeightEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF5FAF9),
        topBar = {
            TopAppBar(
                title = {
                    Text("Histórico de peso", fontWeight = FontWeight.Bold)
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
                        text = "Acompanhe as pesagens e registre a evolução do seu pet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PetPassMint),
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
                                text = "Registrar peso",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PetPassGreenDark,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        OutlinedTextField(
                            value = state.weight,
                            onValueChange = { onEvent(WeightEvent.WeightChanged(it)) },
                            label = { Text("Peso (kg) *") },
                            placeholder = { Text("Ex.: 12,5") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.MonitorWeight,
                                    contentDescription = null,
                                    tint = PetPassGreen
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            colors = petPassTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.measuredAt,
                            onValueChange = {
                                onEvent(WeightEvent.MeasuredAtChanged(it.filter(Char::isDigit).take(8)))
                            },
                            label = { Text("Data da pesagem *") },
                            placeholder = { Text("dd/mm/aaaa") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = PetPassGreen
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            visualTransformation = DateVisualTransformation,
                            colors = petPassTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.veterinarian,
                            onValueChange = { onEvent(WeightEvent.VeterinarianChanged(it)) },
                            label = { Text("Veterinário ou clínica (opcional)") },
                            singleLine = true,
                            colors = petPassTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.notes,
                            onValueChange = { onEvent(WeightEvent.NotesChanged(it)) },
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
                            onClick = { onEvent(WeightEvent.Save) },
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
                                    color = Color.White
                                )
                                Text("Salvando...", modifier = Modifier.padding(start = 8.dp))
                            } else {
                                Text("Salvar peso")
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Histórico (${state.records.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PetPassGreenDark
                )
            }

            if (state.records.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PetPassMint)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonitorWeight,
                                contentDescription = null,
                                tint = PetPassGreen,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Nenhuma pesagem registrada",
                                fontWeight = FontWeight.SemiBold,
                                color = PetPassGreenDark
                            )
                            Text(
                                text = "Os registros de peso aparecerão aqui.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(state.records, key = { it.id }) { record ->
                    WeightHistoryCard(record)
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun petPassTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PetPassGreen,
    focusedLabelColor = PetPassGreen,
    cursorColor = PetPassGreen,
    focusedLeadingIconColor = PetPassGreen
)

@Composable
private fun WeightHistoryCard(record: WeightRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PetPassMint)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MonitorWeight,
                    contentDescription = null,
                    tint = PetPassGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "${DecimalFormat("0.##", DecimalFormatSymbols(Locale("pt", "BR")).apply {
                        decimalSeparator = ','
                    }).format(record.weightKg)} kg",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PetPassGreenDark,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(
                text = "Pesado em ${record.measuredAt}",
                style = MaterialTheme.typography.bodyMedium
            )
            record.veterinarian?.let { veterinarian ->
                Text(
                    text = veterinarian,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            record.notes?.let { notes ->
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
