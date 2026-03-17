package ru.kima.sonar.server.data.user.mappers

import ru.kima.sonar.server.data.user.model.portfolio.PortfolioEntry
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioEntryEntity
import kotlin.time.Instant

internal fun PortfolioEntryEntity.toDomainModel(): PortfolioEntry = PortfolioEntry(
    id = id.value,
    portfolioId = portfolioId,
    securityUid = securityUid,
    name = name,
    targetDeviation = targetDeviation,
    lowPrice = lowPrice,
    highPrice = highPrice,
    note = note,
    enabled = enabled,
    shouldNotify = shouldNotify,
    lastUnboundUpdate = Instant.fromEpochMilliseconds(lastUnboundUpdate),
    lastUnboundUpdatePrice = lastUnboundUpdatePrice
)

internal fun PortfolioEntryEntity.putInside(domainObject: PortfolioEntry) {
    portfolioId = domainObject.portfolioId
    securityUid = domainObject.securityUid
    name = domainObject.name
    lowPrice = domainObject.lowPrice
    highPrice = domainObject.highPrice
    note = domainObject.note
    enabled = domainObject.enabled
    shouldNotify = domainObject.shouldNotify
    lastUnboundUpdate = domainObject.lastUnboundUpdate.toEpochMilliseconds()
    lastUnboundUpdatePrice = domainObject.lastUnboundUpdatePrice
}