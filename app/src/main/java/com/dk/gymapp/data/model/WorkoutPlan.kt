package com.dk.gymapp.data.model

data class WorkoutExercise(
    val exerciseId: String,
    val exerciseName: String,
    val targetSets: Int = 3,
    val targetReps: String = "10-12",
    val targetWeightKg: Double = 0.0,
    val restSeconds: Int = 60,
    val videoFileName: String = ""
)

data class WorkoutPlan(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val category: String, // Beginner, Muscle Building, Fat Loss, Strength Training, Full Body, Chest, Back, Arms, Legs, Core, Cardio, Custom
    val difficulty: String = "Intermediate", // Beginner, Intermediate, Advanced
    val durationMinutes: Int = 45,
    val muscleGroups: List<String> = emptyList(),
    val equipment: String = "Gym Equipment",
    val description: String = "",
    val exercises: List<WorkoutExercise> = emptyList(),
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false
)
