package ru.kima.sonar.server.data.user.mappers

import ru.kima.sonar.server.data.user.model.portfolio.PortfolioEntry
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioEntryEntity
import ru.kima.sonar.server.data.user.scema.portfolio.StopLossEntity
import ru.kima.sonar.server.data.user.scema.portfolio.TakeProfitEntity
import kotlin.time.Instant

internal fun PortfolioEntryEntity.toDomainModel(): PortfolioEntry = PortfolioEntry(
    id = id.value,
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
    lastUnboundUpdate = Instant.fromEpochMilliseconds(lastUnboundUpdate),
    lastUnboundUpdatePrice = lastUnboundUpdatePrice,
    stopLosses = stopLosses.map { it.toDomainModel() },
    takeProfits = takeProfits.map { it.toDomainModel() }
)

internal fun PortfolioEntryEntity.putInside(domainObject: PortfolioEntry) {
    portfolioId = domainObject.portfolioId
    securityUid = domainObject.securityUid
    name = domainObject.name
    ticker = domainObject.ticker
    securityType = domainObject.securityType
    targetDeviation = domainObject.targetDeviation
    lowPrice = domainObject.lowPrice
    highPrice = domainObject.highPrice
    note = domainObject.note
    enabled = domainObject.enabled
    shouldNotify = domainObject.shouldNotify
    lastUnboundUpdate = domainObject.lastUnboundUpdate.toEpochMilliseconds()
    lastUnboundUpdatePrice = domainObject.lastUnboundUpdatePrice

    domainObject.stopLosses.forEach { stopLoss ->
        StopLossEntity.findByIdAndUpdate(stopLoss.id) {
            it.putInside(stopLoss)
        }
    }

    domainObject.takeProfits.forEach { takeProfit ->
        TakeProfitEntity.findByIdAndUpdate(takeProfit.id) {
            it.putInside(takeProfit)
        }
    }
}