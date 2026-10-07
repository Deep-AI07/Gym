package com.dk.gymapp.data.model

data class UserProfile(
    val name: String = "Alex Morgan",
    val tagline: String = "Train Hard. Stay Strong.",
    val age: Int = 26,
    val heightCm: Double = 180.0,
    val weightKg: Double = 78.0,
    val fitnessGoal: String = "Build Muscle", // Build Muscle, Lose Weight, Improve Strength, Improve Endurance, Stay Fit, General Fitness
    val experienceLevel: String = "Intermediate", // Beginner, Intermediate, Advanced, Elite
    val preferredWorkoutDays: Int = 5,
    val useKg: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val darkTheme: Boolean = true,
    val defaultRestSeconds: Int = 60
)
