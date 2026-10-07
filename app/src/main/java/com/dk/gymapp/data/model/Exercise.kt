package com.dk.gymapp.data.model

data class Exercise(
    val id: String,
    val name: String,
    val muscleGroup: String,
    val secondaryMuscles: List<String> = emptyList(),
    val equipment: String = "Bodyweight",
    val difficulty: String = "Beginner", // Beginner, Intermediate, Advanced, or All Levels
    val instructions: List<String> = emptyList(),
    val setsRecommendation: Int = 3,
    val repsRecommendation: String = "8-12",
    val restTimeSeconds: Int = 60,
    val safetyTips: List<String> = emptyList(),
    val commonMistakes: List<String> = emptyList(),
    val videoFileName: String = "",
    val isFavorite: Boolean = false,
    val photoUrl: String = "",
    val videoUrl: String = "",
    val beginnerGuide: String = "",
    val intermediateGuide: String = "",
    val advancedGuide: String = ""
)
