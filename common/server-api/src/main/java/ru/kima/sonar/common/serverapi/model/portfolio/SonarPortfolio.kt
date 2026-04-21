package ru.kima.sonar.common.serverapi.model.portfolio

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.dto.portfolio.response.ListItemPortfolioEntry

@Serializable
data class SonarPortfolio(
    val id: Long,
    val name: String,
    val entries: List<ListItemPortfolioEntry>,
)