package ru.kima.sonar.common.serverapi.dto.portfolio.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePortfolioRequest(
    val id: String,
    val name: String,
)
