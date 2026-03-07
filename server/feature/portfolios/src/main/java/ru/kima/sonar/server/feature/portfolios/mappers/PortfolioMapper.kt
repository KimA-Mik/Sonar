package ru.kima.sonar.server.feature.portfolios.mappers

import ru.kima.sonar.common.serverapi.dto.portfolio.response.ListItemPortfolio
import ru.kima.sonar.common.serverapi.dto.portfolio.response.ListItemPortfolioEntry
import ru.kima.sonar.common.serverapi.dto.portfolio.response.PortfolioResponse
import ru.kima.sonar.server.data.user.model.portfolio.Portfolio
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioEntry
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioWithEntries
import java.math.BigDecimal
import java.math.BigInteger

fun Portfolio.toDto() = ListItemPortfolio(
    id = id,
    name = name,
)

private val emptyPrice = BigDecimal(BigInteger.ZERO)
fun PortfolioWithEntries.toDto(prices: Map<String, BigDecimal>) = PortfolioResponse(
    id = portfolio.id,
    name = portfolio.name,
    entries = entries.map { it.toDto(prices[it.securityUid] ?: emptyPrice) }
)

fun PortfolioEntry.toDto(price: BigDecimal) = ListItemPortfolioEntry(
    id = id,
    uid = securityUid,
    name = name,
    price = price,
    lowPrice = lowPrice,
    highPrice = highPrice,
    note = note
)