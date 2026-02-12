package ru.kima.sonar.server.data.user.model

data class User(
    val id: Long,
    val email: String,
    val passwordHash: String,
)
