package ru.kima.sonar.server.data.user.model.portfolio

data class Portfolio(
    val id: Long,
    val userId: Long,
    val name: String,
)