package ru.kima.sonar.common.serverapi.dto.portfolio.response

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioResponse(
    val id: Long,
    val name: String,
    val entries: List<ListItemPortfolioEntry>,
)