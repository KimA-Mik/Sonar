package ru.kima.sonar.common.serverapi.dto.portfolio.request

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.util.BigDecimalJson

@Serializable
data class UpdatePortfolioEntryRequest(
    val name: String,
    val targetDeviation: BigDecimalJson,
    val lowPrice: BigDecimalJson,
    val highPrice: BigDecimalJson,
    val note: String
)
