package com.dk.gymapp

import androidx.compose.runtime.Composable
import com.dk.gymapp.data.model.Exercise

@Composable
fun ExerciseDetailScreen(
    exercise: Exercise,
    onBack: () -> Unit = {},
    onToggleFavorite: (String) -> Unit = {}
) {
    com.dk.gymapp.ui.screens.exercises.ExerciseDetailScreen(
        exercise = exercise,
        onBack = onBack,
        onToggleFavorite = onToggleFavorite
    )
}