package com.dk.gymapp.data.model

data class CompletedSet(
    val setNumber: Int,
    val reps: Int,
    val weightKg: Double,
    val completed: Boolean = true
)

data class WorkoutHistory(
    val id: Long = 0,
    val workoutId: String,
    val workoutTitle: String,
    val dateMillis: Long,
    val durationSeconds: Int,
    val caloriesBurned: Int,
    val exercisesCompleted: Int,
    val totalSets: Int,
    val notes: String = "",
    val rating: Int = 5
)
