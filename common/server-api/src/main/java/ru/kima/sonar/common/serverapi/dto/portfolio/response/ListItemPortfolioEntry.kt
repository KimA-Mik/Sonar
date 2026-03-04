package ru.kima.sonar.common.serverapi.dto.portfolio.response

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.util.BigDecimalJson

@Serializable
data class ListItemPortfolioEntry(
    val id: Long,
    val uid: String,
    val name: String,
    val price: BigDecimalJson,
    val lowPrice: BigDecimalJson,
    val highPrice: BigDecimalJson,
    val note: String
)