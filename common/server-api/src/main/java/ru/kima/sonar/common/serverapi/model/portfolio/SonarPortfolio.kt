package ru.kima.sonar.common.serverapi.model.portfolio

import kotlinx.serialization.Serializable

@Serializable
data class SonarPortfolio(
    val id: Long,
    val name: String,
    val entries: List<PortfolioEntry>,
)