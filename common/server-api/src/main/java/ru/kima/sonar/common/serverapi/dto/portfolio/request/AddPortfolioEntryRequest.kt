package ru.kima.sonar.common.serverapi.dto.portfolio.request

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.model.portfolio.StopLoss
import ru.kima.sonar.common.serverapi.model.portfolio.TakeProfit
import ru.kima.sonar.common.serverapi.util.BigDecimalJson

@Serializable
data class AddPortfolioEntryRequest(
    val entries: List<Entry>
) {
    @Serializable
    data class Entry(
        val securityUid: String,
        val name: String,
        val targetDeviation: BigDecimalJson,
        val stopLosses: List<StopLoss>,
        val takeProfits: List<TakeProfit>
    )
}