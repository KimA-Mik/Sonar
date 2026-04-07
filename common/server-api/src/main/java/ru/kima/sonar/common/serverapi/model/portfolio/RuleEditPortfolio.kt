package ru.kima.sonar.common.serverapi.model.portfolio

import kotlinx.serialization.Serializable

@Serializable
data class RuleEditPortfolio(
    val id: Long,
    val name: String,
    val rule: SonarRule?
)
