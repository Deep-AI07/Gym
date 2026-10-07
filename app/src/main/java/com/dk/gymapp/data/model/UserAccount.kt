package com.dk.gymapp.data.model

enum class UserRole {
    ADMIN,
    ATHLETE
}

data class UserAccount(
    val id: String,
    val username: String,
    val password: String = "",
    val displayName: String,
    val role: UserRole = UserRole.ATHLETE
)
