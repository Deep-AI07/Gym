package com.dk.gymapp.ui.screens.exercises

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dk.gymapp.data.model.Exercise
import com.dk.gymapp.ui.components.*
import com.dk.gymapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(
    exercises: List<Exercise>,
    onExerciseClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    isAdmin: Boolean = false,
    onAddExerciseClick: (() -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscle by remember { mutableStateOf("All") }
    var selectedDifficulty by remember { mutableStateOf("All") }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    val muscleGroups = listOf("All", "Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Cardio")
    val difficulties = listOf("All", "Beginner", "Intermediate", "Advanced")

    val filteredExercises = remember(searchQuery, selectedMuscle, selectedDifficulty, showFavoritesOnly, exercises) {
        exercises.filter { ex ->
            val matchesQuery = searchQuery.isBlank() ||
                    ex.name.contains(searchQuery, ignoreCase = true) ||
                    ex.muscleGroup.contains(searchQuery, ignoreCase = true) ||
                    ex.equipment.contains(searchQuery, ignoreCase = true)

            val matchesMuscle = selectedMuscle == "All" || ex.muscleGroup.equals(selectedMuscle, ignoreCase = true)

            val matchesDifficulty = when (selectedDifficulty) {
                "All" -> true
                else -> ex.difficulty.equals(selectedDifficulty, ignoreCase = true) ||
                        ex.difficulty.equals("All Levels", ignoreCase = true) ||
                        (selectedDifficulty == "Beginner" && ex.beginnerGuide.isNotBlank()) ||
                        (selectedDifficulty == "Intermediate" && ex.intermediateGuide.isNotBlank()) ||
                        (selectedDifficulty == "Advanced" && ex.advancedGuide.isNotBlank())
            }

            val matchesFavorite = !showFavoritesOnly || ex.isFavorite

            matchesQuery && matchesMuscle && matchesDifficulty && matchesFavorite
        }
    }

    // Dynamic camera cutout safe insets across various devices (punch hole, teardrop notch, pill)
    val safeCutoutInsets = WindowInsets.safeDrawing.asPaddingValues()
    val safeTop = safeCutoutInsets.calculateTopPadding()
    val safeBottom = safeCutoutInsets.calculateBottomPadding()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = 16.dp + safeCutoutInsets.calculateStartPadding(LocalLayoutDirection.current),
            end = 16.dp + safeCutoutInsets.calculateEndPadding(LocalLayoutDirection.current),
            top = safeTop + 10.dp,
            bottom = 100.dp + safeBottom
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER WITH OPTIONAL ADMIN ADD BUTTON
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "EXERCISE DIRECTORY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Exercise Library",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Black
                    )
                }

                if (isAdmin && onAddExerciseClick != null) {
                    Button(
                        onClick = onAddExerciseClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GymGreen)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add New", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // SEARCH BAR
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search exercises, muscles, equipment...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GymGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                singleLine = true
            )
        }

        // MUSCLE GROUP FILTER
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MUSCLE GROUP",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )

                    // Favorite filter toggle button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showFavoritesOnly = !showFavoritesOnly }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (showFavoritesOnly) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = null,
                            tint = if (showFavoritesOnly) AccentOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Favorites",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (showFavoritesOnly) AccentOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(muscleGroups) { muscle ->
                        val isSelected = muscle == selectedMuscle
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedMuscle = muscle },
                            label = { Text(muscle, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GymGreen,
                                selectedLabelColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // RESPONSIVE DIFFICULTY FILTER CHIPS (LAZY ROW FIX)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "DIFFICULTY LEVEL",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(difficulties) { diff ->
                        val isSelected = diff == selectedDifficulty
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDifficulty = diff },
                            label = { Text(diff, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // COUNT HEADER
        item {
            Text(
                text = "Showing ${filteredExercises.size} exercises",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // EXERCISE CARDS
        if (filteredExercises.isEmpty()) {
            item {
                GymCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No exercises match your search",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try clearing filters or search terms.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredExercises, key = { it.id }) { exercise ->
                GymCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExerciseClick(exercise.id) },
                    borderColor = if (exercise.isFavorite) AccentOrange.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                MuscleBadge(muscle = exercise.muscleGroup, isPrimary = true)
                                DifficultyBadge(difficulty = exercise.difficulty)

                                if (exercise.videoFileName.isNotBlank() || exercise.videoUrl.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = AccentCyan.copy(alpha = 0.15f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayCircle,
                                                contentDescription = null,
                                                tint = AccentCyan,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "VIDEO",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = AccentCyan,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = exercise.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Equipment: ${exercise.equipment}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "${exercise.setsRecommendation} sets × ${exercise.repsRecommendation} reps • ${exercise.restTimeSeconds}s rest",
                                style = MaterialTheme.typography.bodySmall,
                                color = GymGreenLight,
                                fontWeight = FontWeight.Medium
                            )

                            if (exercise.beginnerGuide.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Supports Beginner, Intermediate & Advanced",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GymGreen.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = { onToggleFavorite(exercise.id) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (exercise.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Favorite",
                                tint = if (exercise.isFavorite) AccentOrange else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
