package com.dk.gymapp.data.model

data class BodyMeasurement(
    val id: Long = 0,
    val dateMillis: Long,
    val weightKg: Double,
    val chestCm: Double,
    val waistCm: Double,
    val armsCm: Double,
    val legsCm: Double
)

data class PersonalRecord(
    val id: Long = 0,
    val exerciseName: String,
    val recordValue: Double,
    val unit: String = "kg",
    val dateMillis: Long
)
