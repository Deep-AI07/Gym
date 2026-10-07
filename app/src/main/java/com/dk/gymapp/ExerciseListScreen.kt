package com.dk.gymapp

import androidx.compose.runtime.Composable
import com.dk.gymapp.data.model.Exercise

@Composable
fun ExerciseListScreen(
    onExerciseClick: (Exercise) -> Unit,
    exercises: List<Exercise> = exerciseList,
    onToggleFavorite: (String) -> Unit = {}
) {
    com.dk.gymapp.ui.screens.exercises.ExerciseListScreen(
        exercises = exercises,
        onExerciseClick = { exId ->
            val exercise = exercises.find { it.id == exId } ?: exercises.find { it.name == exId }
            if (exercise != null) {
                onExerciseClick(exercise)
            }
        },
        onToggleFavorite = onToggleFavorite
    )
}