package com.leandro.petpass.presentation.addpet

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.leandro.petpass.presentation.common.DateVisualTransformation

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddPetScreen(
    state: AddPetUiState,
    onEvent: (AddPetEvent) -> Unit,
    onBack: () -> Unit
) {
    if (state.isSaved) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text(if (state.isEditing) "Pet Atualizado com Sucesso! 🐾" else "Pet Cadastrado com Sucesso! 🐾") },
            text = { Text(if (state.isEditing) "As alterações no RG digital e dados do pet foram salvas." else "O RG digital e a carteirinha do seu amiguinho já estão prontos para uso.") },
            confirmButton = {
                Button(onClick = onBack) {
                    Text(if (state.isEditing) "Voltar" else "Ver RG Digital")
                }
            }
        )
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> onEvent(AddPetEvent.PhotoSelected(uri?.toString())) }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Editar pet" else "Adicionar pet") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Crie o RG digital do seu melhor amigo.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = state.photoUri?.let(Uri::parse),
                    contentDescription = "Foto do pet",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape),
                    placeholder = null
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (state.photoUri == null) "Foto do pet" else "Foto selecionada",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedButton(onClick = { photoPicker.launch("image/*") }) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (state.photoUri == null) "Escolher foto" else "Trocar foto",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.name,
                onValueChange = { onEvent(AddPetEvent.NameChanged(it)) },
                label = { Text("Nome *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Espécie *", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Cão", "Gato", "Outro").forEach { species ->
                        FilterChip(
                            selected = state.species == species,
                            onClick = { onEvent(AddPetEvent.SpeciesChanged(species)) },
                            label = { Text(species) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.breed,
                onValueChange = { onEvent(AddPetEvent.BreedChanged(it)) },
                label = { Text("Raça (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.birthDate,
                onValueChange = {
                    onEvent(AddPetEvent.BirthDateChanged(it.filter(Char::isDigit).take(8)))
                },
                label = { Text("Nascimento (dd/mm/aaaa) *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = DateVisualTransformation,
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Sexo *", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Macho", "Fêmea").forEach { gender ->
                        FilterChip(
                            selected = state.gender == gender,
                            onClick = { onEvent(AddPetEvent.GenderChanged(gender)) },
                            label = { Text(gender) }
                        )
                    }
                }

            }

            OutlinedTextField(
                value = state.rga,
                onValueChange = { onEvent(AddPetEvent.RgaChanged(it)) },
                label = { Text("RGA - Registro Geral Animal (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.microchip,
                onValueChange = { onEvent(AddPetEvent.MicrochipChanged(it)) },
                label = { Text("Microchip (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Dados do tutor",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = state.ownerName,
                onValueChange = { onEvent(AddPetEvent.OwnerNameChanged(it)) },
                label = { Text("Nome do tutor *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.emergencyPhone,
                onValueChange = { onEvent(AddPetEvent.EmergencyPhoneChanged(it)) },
                label = { Text("Telefone para emergência *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            state.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = { onEvent(AddPetEvent.Save) },
                enabled = !state.isSaving,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text("Salvando...", modifier = Modifier.padding(start = 8.dp))
                } else {
                    Text(if (state.isEditing) "Salvar alterações" else "Salvar pet")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
