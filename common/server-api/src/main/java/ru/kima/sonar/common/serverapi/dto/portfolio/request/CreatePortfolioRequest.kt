package ru.kima.sonar.common.serverapi.dto.portfolio.request

import kotlinx.serialization.Serializable

@Serializable
data class CreatePortfolioRequest(
    val name: String,
)
