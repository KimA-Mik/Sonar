package ru.kima.sonar.common.serverapi.dto.portfolio.response

import kotlinx.serialization.Serializable

@Serializable
data class ListItemPortfolio(
    val id: Long,
    val name: String
)
