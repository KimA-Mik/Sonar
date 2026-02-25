package ru.kima.sonar.server.data.user.mappers

import ru.kima.sonar.server.data.user.model.portfolio.PortfolioEntry
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioEntryEntity
import java.math.BigDecimal

internal fun PortfolioEntryEntity.toDomainModel(): PortfolioEntry = PortfolioEntry(
    id = id.value,
    portfolioId = portfolioId,
    securityUid = securityUid,
    name = name,
    lowPrice = lowPrice.toBigInteger(),
    highPrice = highPrice.toBigInteger(),
    note = note
)

internal fun PortfolioEntryEntity.putInside(domainObject: PortfolioEntry) {
    portfolioId = domainObject.portfolioId
    securityUid = domainObject.securityUid
    name = domainObject.name
    lowPrice = BigDecimal(domainObject.lowPrice)
    highPrice = BigDecimal(domainObject.highPrice)
    note = domainObject.note
}