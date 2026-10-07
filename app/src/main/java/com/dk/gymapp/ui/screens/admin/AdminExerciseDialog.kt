package com.dk.gymapp.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dk.gymapp.data.model.Exercise
import com.dk.gymapp.ui.theme.*
import java.util.UUID

@Composable
fun AdminExerciseDialog(
    exerciseToEdit: Exercise? = null,
    onDismiss: () -> Unit,
    onSave: (Exercise) -> Unit,
    onDelete: ((String) -> Unit)? = null
) {
    var name by remember { mutableStateOf(exerciseToEdit?.name ?: "") }
    var muscleGroup by remember { mutableStateOf(exerciseToEdit?.muscleGroup ?: "Chest") }
    var equipment by remember { mutableStateOf(exerciseToEdit?.equipment ?: "Barbell") }
    var difficulty by remember { mutableStateOf(exerciseToEdit?.difficulty ?: "All Levels") }
    var instructionsText by remember { mutableStateOf(exerciseToEdit?.instructions?.joinToString("\n") ?: "") }
    var sets by remember { mutableStateOf("${exerciseToEdit?.setsRecommendation ?: 3}") }
    var reps by remember { mutableStateOf(exerciseToEdit?.repsRecommendation ?: "8-12") }
    var rest by remember { mutableStateOf("${exerciseToEdit?.restTimeSeconds ?: 60}") }
    var videoUrl by remember { mutableStateOf(exerciseToEdit?.videoUrl ?: "") }
    var photoUrl by remember { mutableStateOf(exerciseToEdit?.photoUrl ?: "") }
    var beginnerGuide by remember { mutableStateOf(exerciseToEdit?.beginnerGuide ?: "") }
    var intermediateGuide by remember { mutableStateOf(exerciseToEdit?.intermediateGuide ?: "") }
    var advancedGuide by remember { mutableStateOf(exerciseToEdit?.advancedGuide ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (exerciseToEdit != null) "Admin: Edit Exercise" else "Admin: Add New Exercise",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Exercise Name *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = muscleGroup,
                            onValueChange = { muscleGroup = it },
                            label = { Text("Muscle Group") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = equipment,
                            onValueChange = { equipment = it },
                            label = { Text("Equipment") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sets,
                            onValueChange = { sets = it },
                            label = { Text("Sets") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = reps,
                            onValueChange = { reps = it },
                            label = { Text("Reps") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = rest,
                            onValueChange = { rest = it },
                            label = { Text("Rest (s)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = videoUrl,
                        onValueChange = { videoUrl = it },
                        label = { Text("Video Tutorial URL (YouTube/Web)") },
                        placeholder = { Text("https://www.youtube.com/watch?v=...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = photoUrl,
                        onValueChange = { photoUrl = it },
                        label = { Text("Photo Image URL") },
                        placeholder = { Text("https://...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = beginnerGuide,
                        onValueChange = { beginnerGuide = it },
                        label = { Text("Beginner Level Guide") },
                        placeholder = { Text("3 sets × 10-12 reps, light weight...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = intermediateGuide,
                        onValueChange = { intermediateGuide = it },
                        label = { Text("Intermediate Level Guide") },
                        placeholder = { Text("4 sets × 8-10 reps, 70% 1RM...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = advancedGuide,
                        onValueChange = { advancedGuide = it },
                        label = { Text("Advanced Level Guide") },
                        placeholder = { Text("5 sets × 3-5 reps heavy power...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = instructionsText,
                        onValueChange = { instructionsText = it },
                        label = { Text("Instructions (one step per line)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val exercise = Exercise(
                            id = exerciseToEdit?.id ?: ("ex_" + UUID.randomUUID().toString().take(8)),
                            name = name.trim(),
                            muscleGroup = muscleGroup.trim(),
                            equipment = equipment.trim(),
                            difficulty = "All Levels",
                            instructions = instructionsText.lines().filter { it.isNotBlank() },
                            setsRecommendation = sets.toIntOrNull() ?: 3,
                            repsRecommendation = reps.trim().ifBlank { "8-12" },
                            restTimeSeconds = rest.toIntOrNull() ?: 60,
                            safetyTips = exerciseToEdit?.safetyTips ?: emptyList(),
                            commonMistakes = exerciseToEdit?.commonMistakes ?: emptyList(),
                            videoFileName = exerciseToEdit?.videoFileName ?: "",
                            isFavorite = exerciseToEdit?.isFavorite ?: false,
                            photoUrl = photoUrl.trim(),
                            videoUrl = videoUrl.trim(),
                            beginnerGuide = beginnerGuide.trim(),
                            intermediateGuide = intermediateGuide.trim(),
                            advancedGuide = advancedGuide.trim()
                        )
                        onSave(exercise)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GymGreen)
            ) {
                Text("Save Exercise", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (exerciseToEdit != null && onDelete != null) {
                    TextButton(onClick = { onDelete(exerciseToEdit.id) }) {
                        Text("Delete", color = AccentRed, fontWeight = FontWeight.Bold)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
