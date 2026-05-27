package ru.kima.sonar.server.data.user.model.portfolio

import ru.kima.sonar.common.serverapi.model.portfolio.SecurityType
import java.math.BigDecimal
import kotlin.time.Instant

data class PortfolioEntry(
    val id: Long,
    val portfolioId: Long,
    val securityUid: String,
    val name: String,
    val ticker: String,
    val securityType: SecurityType,
    val targetDeviation: BigDecimal,
    val lowPrice: BigDecimal,
    val highPrice: BigDecimal,
    val note: String,
    val enabled: Boolean,
    val shouldNotify: Boolean,
    val lastUnboundUpdate: Instant,
    val lastUnboundUpdatePrice: BigDecimal,
    val stopLosses: List<StopLoss>,
    val takeProfits: List<TakeProfit>
) {
    companion object {
        fun default(
            id: Long = 0,
            portfolioId: Long,
            securityUid: String,
            name: String,
            ticker: String,
            securityType: SecurityType,
            targetDeviation: BigDecimal,
            lowPrice: BigDecimal,
            highPrice: BigDecimal,
            note: String,
            enabled: Boolean = true,
            shouldNotify: Boolean = true,
            lastUnboundUpdate: Instant = Instant.DISTANT_PAST,
            lastUnboundUpdatePrice: BigDecimal = BigDecimal.ZERO,
            stopLosses: List<StopLoss> = emptyList(),
            takeProfits: List<TakeProfit> = emptyList()
        ) = PortfolioEntry(
            id = id,
            portfolioId = portfolioId,
            securityUid = securityUid,
            name = name,
            ticker = ticker,
            securityType = securityType,
            targetDeviation = targetDeviation,
            lowPrice = lowPrice,
            highPrice = highPrice,
            note = note,
            enabled = enabled,
            shouldNotify = shouldNotify,
            lastUnboundUpdate = lastUnboundUpdate,
            lastUnboundUpdatePrice = lastUnboundUpdatePrice,
            stopLosses = stopLosses,
            takeProfits = takeProfits
        )
    }
}