package com.leandro.petpass.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.leandro.petpass.PetPassContainer
import com.leandro.petpass.domain.model.Pet
import com.leandro.petpass.presentation.addpet.AddPetScreen
import com.leandro.petpass.presentation.addpet.AddPetViewModel
import com.leandro.petpass.presentation.exams.ExamViewModel
import com.leandro.petpass.presentation.exams.ExamsScreen
import com.leandro.petpass.presentation.home.toHomeUiModel
import com.leandro.petpass.presentation.home.PetPassHomeScreen
import com.leandro.petpass.presentation.vaccines.VaccineViewModel
import com.leandro.petpass.presentation.vaccines.VaccinesScreen
import com.leandro.petpass.presentation.weight.WeightScreen
import com.leandro.petpass.presentation.weight.WeightViewModel
import kotlinx.coroutines.launch

@Composable
fun PetPassAppNavHost(
    container: PetPassContainer,
    isUserLoggedIn: Boolean = true
) {
    val navController = rememberNavController()
    val pets by remember(container) {
        container.observePetsUseCase()
    }.collectAsState(initial = null as List<Pet>?)

    val latestWeights by remember(container) {
        container.observeLatestWeightsUseCase()
    }.collectAsState(initial = emptyMap())

    val latestExamDates by remember(container) {
        container.observeLatestExamDatesUseCase()
    }.collectAsState(initial = emptyMap())

    val vaccines by remember(container) {
        container.observeAllVaccinesUseCase()
    }.collectAsState(initial = emptyList())

    val exams by remember(container) {
        container.observeAllExamsUseCase()
    }.collectAsState(initial = emptyList())

    val startRoute = if (isUserLoggedIn) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startRoute
    ) {
        composable("login") {}

        // Rota 2: Tela Home com indicador de carregamento inicial para evitar flash de "vazio"
        composable("home") {
            val currentPets = pets
            if (currentPets == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF00695C))
                }
            } else {
                var selectedPetIndex by remember { mutableIntStateOf(0) }
                val scope = rememberCoroutineScope()
                val homePets = remember(currentPets, latestWeights, latestExamDates, vaccines, exams) {
                    currentPets.map { pet ->
                        pet.toHomeUiModel(
                            currentWeightKg = latestWeights[pet.id],
                            lastVetVisit = latestExamDates[pet.id] ?: "Sem consultas",
                            vaccines = vaccines,
                            exams = exams
                        )
                    }
                }

                PetPassHomeScreen(
                    pets = homePets,
                    selectedPetIndex = selectedPetIndex,
                    onSelectPet = { newIndex -> selectedPetIndex = newIndex },
                    onAddPetClick = { navController.navigate("add_pet") },
                    onShareRgClick = {},
                    onEditPetClick = { petId ->
                        navController.navigate("edit_pet/$petId")
                    },
                    onNavigateToVaccines = { petId ->
                        navController.navigate("vaccines/$petId")
                    },
                    onNavigateToWeight = { petId ->
                        navController.navigate("weight/$petId")
                    },
                    onNavigateToExams = { petId ->
                        navController.navigate("exams/$petId")
                    },
                    onCompleteReminder = { reminderId ->
                        scope.launch {
                            when {
                                reminderId.startsWith("vaccine:") ->
                                    container.vaccineRepositoryClearNextDueDate(
                                        reminderId.removePrefix("vaccine:")
                                    )
                                reminderId.startsWith("exam:") ->
                                    container.examRepositoryClearNextDueDate(
                                        reminderId.removePrefix("exam:")
                                    )
                            }
                        }
                    }
                )
            }
        }

        // Rota 3: Cadastro de Novo Pet
        composable("add_pet") {
            val addPetViewModel: AddPetViewModel = viewModel(
                factory = remember(container) {
                    AddPetViewModel.Factory(container.savePetUseCase)
                }
            )
            val state by addPetViewModel.uiState.collectAsState()

            AddPetScreen(
                state = state,
                onEvent = addPetViewModel::onEvent,
                onBack = { navController.popBackStack() }
            )
        }

        // Rota 3.1: Edição de Pet Existente
        composable("edit_pet/{petId}") { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId") ?: ""
            val petToEdit = pets?.firstOrNull { it.id == petId }

            val addPetViewModel: AddPetViewModel = viewModel(
                key = "edit_pet-$petId",
                factory = remember(container, petToEdit) {
                    AddPetViewModel.Factory(container.savePetUseCase, petToEdit)
                }
            )
            val state by addPetViewModel.uiState.collectAsState()

            AddPetScreen(
                state = state,
                onEvent = addPetViewModel::onEvent,
                onBack = { navController.popBackStack() }
            )
        }

        // Rota 4: Carteirinha de Vacinas
        composable("vaccines/{petId}") { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId") ?: ""
            val vaccineViewModel: VaccineViewModel = viewModel(
                key = "vaccines-$petId",
                factory = remember(container, petId) {
                    VaccineViewModel.Factory(
                        petId = petId,
                        observeVaccines = container.observeVaccinesUseCase,
                        saveVaccine = container.saveVaccineUseCase
                    )
                }
            )
            val state by vaccineViewModel.uiState.collectAsState()
            val petName = pets?.firstOrNull { it.id == petId }?.name ?: "Pet"

            VaccinesScreen(
                petName = petName,
                state = state,
                onEvent = vaccineViewModel::onEvent,
                onBack = { navController.popBackStack() }
            )
        }

        composable("weight/{petId}") { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId") ?: ""
            val weightViewModel: WeightViewModel = viewModel(
                key = "weight-$petId",
                factory = remember(container, petId) {
                    WeightViewModel.Factory(
                        petId = petId,
                        observeWeightRecords = container.observeWeightRecordsUseCase,
                        saveWeightRecord = container.saveWeightRecordUseCase
                    )
                }
            )
            val state by weightViewModel.uiState.collectAsState()
            val petName = pets?.firstOrNull { it.id == petId }?.name ?: "Pet"

            WeightScreen(
                petName = petName,
                state = state,
                onEvent = weightViewModel::onEvent,
                onBack = { navController.popBackStack() }
            )
        }

        composable("exams/{petId}") { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId") ?: ""
            val examViewModel: ExamViewModel = viewModel(
                key = "exams-$petId",
                factory = remember(container, petId) {
                    ExamViewModel.Factory(
                        petId = petId,
                        observeExams = container.observeExamsUseCase,
                        saveExam = container.saveExamUseCase
                    )
                }
            )
            val state by examViewModel.uiState.collectAsState()
            val petName = pets?.firstOrNull { it.id == petId }?.name ?: "Pet"

            ExamsScreen(
                petName = petName,
                state = state,
                onEvent = examViewModel::onEvent,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
