package ru.kima.sonar.server.data.user.model.portfolio

data class LightPortfolioWithRule(
    val id: Long,
    val userId: Long,
    val name: String,
    val rule: PortfolioRule?
)
