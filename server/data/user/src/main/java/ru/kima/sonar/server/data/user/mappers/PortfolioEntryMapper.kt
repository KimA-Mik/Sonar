package ru.kima.sonar.server.data.user.mappers

import ru.kima.sonar.server.data.user.model.portfolio.PortfolioEntry
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioEntryEntity

internal fun PortfolioEntryEntity.toDomainModel(): PortfolioEntry = PortfolioEntry(
    id = id.value,
    portfolioId = portfolioId,
    securityUid = securityUid,
    name = name,
    lowPrice = lowPrice,
    highPrice = highPrice,
    note = note
)

internal fun PortfolioEntryEntity.putInside(domainObject: PortfolioEntry) {
    portfolioId = domainObject.portfolioId
    securityUid = domainObject.securityUid
    name = domainObject.name
    lowPrice = domainObject.lowPrice
    highPrice = domainObject.highPrice
    note = domainObject.note
}