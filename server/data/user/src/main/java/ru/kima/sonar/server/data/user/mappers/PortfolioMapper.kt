package ru.kima.sonar.server.data.user.mappers

import ru.kima.sonar.server.data.user.model.portfolio.Portfolio
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioEntity

internal fun PortfolioEntity.toDomainModel(): Portfolio = Portfolio(
    id = id.value,
    userId = userId,
    name = name
)

internal fun PortfolioEntity.putInside(domainObject: Portfolio) {
    userId = domainObject.userId
    name = domainObject.name
}