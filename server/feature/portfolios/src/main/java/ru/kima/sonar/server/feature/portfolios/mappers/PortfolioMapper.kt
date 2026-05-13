package ru.kima.sonar.server.feature.portfolios.mappers

import ru.kima.sonar.common.serverapi.dto.portfolio.request.AddPortfolioEntryRequest
import ru.kima.sonar.common.serverapi.dto.portfolio.response.ListItemPortfolio
import ru.kima.sonar.common.serverapi.model.portfolio.SonarPortfolio
import ru.kima.sonar.server.data.user.model.portfolio.Portfolio
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioEntry
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioWithEntries
import ru.kima.sonar.server.data.user.model.portfolio.StopLoss
import ru.kima.sonar.server.data.user.model.portfolio.TakeProfit
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.time.Instant

fun Portfolio.toDto() = ListItemPortfolio(
    id = id,
    name = name,
)

private val emptyPrice = BigDecimal(BigInteger.ZERO)
fun PortfolioWithEntries.toDto(prices: Map<String, BigDecimal>) = SonarPortfolio(
    id = portfolio.id,
    name = portfolio.name,
    entries = entries.map { it.toDto(prices[it.securityUid] ?: emptyPrice) }
)

typealias PortfolioEntryDto = ru.kima.sonar.common.serverapi.model.portfolio.PortfolioEntry
typealias StopLossDto = ru.kima.sonar.common.serverapi.model.portfolio.StopLoss
typealias TakeProfitDto = ru.kima.sonar.common.serverapi.model.portfolio.TakeProfit

fun PortfolioEntry.toDto(price: BigDecimal) = PortfolioEntryDto(
    id = id,
    uid = securityUid,
    name = name,
    targetDeviation = targetDeviation,
    price = price,
    lowPrice = lowPrice,
    highPrice = highPrice,
    note = note,
    stopLosses = stopLosses.map { it.toDto() },
    takeProfits = takeProfits.map { it.toDto() },
)

fun StopLoss.toDto() = StopLossDto(
    id = id,
    entryId = entryId,
    price = price,
    note = note
)

fun TakeProfit.toDto() = TakeProfitDto(
    id = id,
    entryId = entryId,
    price = price,
    note = note
)

fun AddPortfolioEntryRequest.Entry.toDomain(portfolioId: Long) = PortfolioEntry(
    id = 0L,
    portfolioId = portfolioId,
    securityUid = securityUid,
    name = name,
    targetDeviation = targetDeviation,
    lowPrice = BigDecimal.ZERO,
    highPrice = BigDecimal.ZERO,
    note = "",
    enabled = true,
    shouldNotify = true,
    lastUnboundUpdate = Instant.DISTANT_PAST,
    lastUnboundUpdatePrice = BigDecimal.ZERO,
    stopLosses = stopLosses.map { it.toDomain() },
    takeProfits = takeProfits.map { it.toDomain() },
)

fun StopLossDto.toDomain(
    id: Long = this.id,
    entryId: Long = this.entryId
) = StopLoss(
    id = id,
    entryId = entryId,
    price = price,
    note = note,
    shouldNotify = true,
    lastUnboundUpdate = Instant.DISTANT_PAST,
    lastUnboundUpdatePrice = BigDecimal.ZERO
)

fun TakeProfitDto.toDomain(
    id: Long = this.id,
    entryId: Long = this.entryId
) = TakeProfit(
    id = id,
    entryId = entryId,
    price = price,
    note = note,
    shouldNotify = true,
    lastUnboundUpdate = Instant.DISTANT_PAST,
    lastUnboundUpdatePrice = BigDecimal.ZERO
)