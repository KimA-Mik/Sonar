package ru.kima.sonar.server.data.user.model.portfolio

import java.math.BigDecimal
import kotlin.time.Instant

data class PortfolioEntry(
    val id: Long,
    val portfolioId: Long,
    val securityUid: String,
    val name: String,
    val targetDeviation: BigDecimal,
    val lowPrice: BigDecimal,
    val highPrice: BigDecimal,
    val note: String,
    val enabled: Boolean,
    val shouldNotify: Boolean,
    val lastUnboundUpdate: Instant,
    val lastUnboundUpdatePrice: BigDecimal,
) {
    companion object {
        fun default(
            id: Long = 0,
            portfolioId: Long,
            securityUid: String,
            name: String,
            targetDeviation: BigDecimal,
            lowPrice: BigDecimal,
            highPrice: BigDecimal,
            note: String,
            enabled: Boolean,
            shouldNotify: Boolean,
            lastUnboundUpdate: Instant,
            lastUnboundUpdatePrice: BigDecimal
        ) = PortfolioEntry(
            id = id,
            portfolioId = portfolioId,
            securityUid = securityUid,
            name = name,
            targetDeviation = targetDeviation,
            lowPrice = lowPrice,
            highPrice = highPrice,
            note = note,
            enabled = enabled,
            shouldNotify = shouldNotify,
            lastUnboundUpdate = lastUnboundUpdate,
            lastUnboundUpdatePrice = lastUnboundUpdatePrice
        )
    }
}