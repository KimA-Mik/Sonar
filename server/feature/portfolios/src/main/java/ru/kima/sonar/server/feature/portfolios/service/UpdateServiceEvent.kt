package ru.kima.sonar.server.feature.portfolios.service

import ru.kima.sonar.common.serverapi.events.BoundPriceEven
import ru.kima.sonar.common.serverapi.events.UnboundPriceEvent
import ru.kima.sonar.common.serverapi.model.LastPrice
import ru.kima.sonar.server.data.user.model.UserAndSessions
import ru.kima.sonar.server.data.user.model.portfolio.Portfolio
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioEntry
import ru.kima.sonar.server.feature.portfolios.service.model.CacheEntry

abstract class UpdateServiceEvent {
    abstract val user: UserAndSessions
    abstract val portfolio: Portfolio

    data class UnboundPriceAlert(
        override val user: UserAndSessions,
        override val portfolio: Portfolio,
        val entry: PortfolioEntry,
        val indicators: CacheEntry,
        val lastPrice: LastPrice,
        val priceType: UnboundPriceEvent.PriceType
    ) : UpdateServiceEvent()

    data class PriceAlert(
        override val user: UserAndSessions,
        override val portfolio: Portfolio,
        val entry: PortfolioEntry,
        val indicators: CacheEntry,
        val lastPrice: LastPrice,
        val priceType: BoundPriceEven.PriceType
    ) : UpdateServiceEvent()
}