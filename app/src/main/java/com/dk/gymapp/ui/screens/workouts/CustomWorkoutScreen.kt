package com.dk.gymapp.ui.screens.workouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dk.gymapp.data.model.Exercise
import com.dk.gymapp.data.model.WorkoutExercise
import com.dk.gymapp.data.model.WorkoutPlan
import com.dk.gymapp.ui.components.*
import com.dk.gymapp.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomWorkoutScreen(
    availableExercises: List<Exercise>,
    onBack: () -> Unit,
    onSaveWorkout: (WorkoutPlan) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Full Body") }
    var difficulty by remember { mutableStateOf("Intermediate") }
    var durationMinutes by remember { mutableIntStateOf(45) }
    var description by remember { mutableStateOf("") }

    val selectedExercises = remember { mutableStateListOf<WorkoutExercise>() }
    var showExercisePicker by remember { mutableStateOf(false) }

    val categories = listOf("Full Body", "Chest", "Back", "Legs", "Arms", "Shoulders", "Core", "Cardio")
    val difficulties = listOf("Beginner", "Intermediate", "Advanced")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Custom Workout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                windowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                ),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
                shadowElevation = 8.dp
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            if (title.isNotBlank() && selectedExercises.isNotEmpty()) {
                                val newPlan = WorkoutPlan(
                                    id = "custom_" + UUID.randomUUID().toString().take(8),
                                    title = title.trim(),
                                    subtitle = subtitle.ifBlank { "Custom routine" },
                                    category = category,
                                    difficulty = difficulty,
                                    durationMinutes = durationMinutes,
                                    muscleGroups = selectedExercises.map { ex ->
                                        availableExercises.find { it.id == ex.exerciseId }?.muscleGroup ?: "Full Body"
                                    }.distinct(),
                                    equipment = "Gym Equipment",
                                    description = description,
                                    exercises = selectedExercises.toList(),
                                    isFavorite = true,
                                    isCustom = true
                                )
                                onSaveWorkout(newPlan)
                            }
                        },
                        enabled = title.isNotBlank() && selectedExercises.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GymGreen)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SAVE CUSTOM WORKOUT",
                            color = Color.Black,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // WORKOUT DETAILS FORM
            item {
                GymCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "WORKOUT DETAILS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Workout Name *") },
                        placeholder = { Text("e.g. Upper Body Hypertrophy") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        label = { Text("Subtitle / Focus") },
                        placeholder = { Text("e.g. 5x5 compound power") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // CATEGORY CHIPS
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.take(4).forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GymGreen,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.drop(4).forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GymGreen,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // DIFFICULTY SELECTION
                    Text(
                        text = "Difficulty Level",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        difficulties.forEach { diff ->
                            FilterChip(
                                selected = difficulty == diff,
                                onClick = { difficulty = diff },
                                label = { Text(diff) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GymGreen,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ESTIMATED DURATION
                    Text(
                        text = "Estimated Duration: $durationMinutes min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = durationMinutes.toFloat(),
                        onValueChange = { durationMinutes = it.toInt() },
                        valueRange = 15f..120f,
                        steps = 20,
                        colors = SliderDefaults.colors(
                            thumbColor = GymGreen,
                            activeTrackColor = GymGreen
                        )
                    )
                }
            }

            // EXERCISES SECTION
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EXERCISES (${selectedExercises.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Add and configure movement sets & reps",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showExercisePicker = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GymGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // SELECTED EXERCISES LIST
            if (selectedExercises.isEmpty()) {
                item {
                    GymCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No exercises added yet", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Click '+ Add' above to choose from library", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                itemsIndexed(selectedExercises) { index, exercise ->
                    GymCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(GymGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${index + 1}", color = GymGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = exercise.exerciseName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${exercise.targetSets} sets • ${exercise.targetReps} reps • ${exercise.restSeconds}s rest",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(
                                onClick = { selectedExercises.removeAt(index) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = AccentRed)
                            }
                        }
                    }
                }
            }
        }
    }

    // EXERCISE PICKER MODAL DIALOG
    if (showExercisePicker) {
        AlertDialog(
            onDismissRequest = { showExercisePicker = false },
            title = { Text("Choose Exercise", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableExercises) { ex ->
                        val alreadyAdded = selectedExercises.any { it.exerciseId == ex.id }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (alreadyAdded) GymGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    if (!alreadyAdded) {
                                        selectedExercises.add(
                                            WorkoutExercise(
                                                exerciseId = ex.id,
                                                exerciseName = ex.name,
                                                targetSets = ex.setsRecommendation,
                                                targetReps = ex.repsRecommendation,
                                                restSeconds = ex.restTimeSeconds,
                                                videoFileName = ex.videoFileName
                                            )
                                        )
                                    }
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(ex.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(ex.muscleGroup, color = GymGreen, fontSize = 12.sp)
                            }
                            if (alreadyAdded) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = GymGreen, modifier = Modifier.size(20.dp))
                            } else {
                                Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showExercisePicker = false }) {
                    Text("Done", color = GymGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
