package ru.kima.sonar.common.serverapi.model.portfolio

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.util.BigDecimalJson

@Serializable
data class PortfolioEntry(
    val id: Long,
    val uid: String,
    val name: String,
    val ticker: String,
    val targetDeviation: BigDecimalJson,
    val price: BigDecimalJson,
    val lowPrice: BigDecimalJson,
    val highPrice: BigDecimalJson,
    val note: String,
    val stopLosses: List<StopLoss>,
    val takeProfits: List<TakeProfit>
)