package ru.kima.sonar.server.feature.portfolios.service.mapper

import ru.kima.sonar.common.serverapi.events.BoundPriceEvent
import ru.kima.sonar.common.serverapi.events.NotificationEvent
import ru.kima.sonar.common.serverapi.events.UnboundPriceEvent
import ru.kima.sonar.server.feature.portfolios.service.UpdateServiceEvent

fun UpdateServiceEvent.toNotificationEvent(): NotificationEvent = when (this) {
    is UpdateServiceEvent.PriceAlert -> toNotificationEvent()
    is UpdateServiceEvent.UnboundPriceAlert -> toNotificationEvent()
}

private fun UpdateServiceEvent.PriceAlert.toNotificationEvent() = BoundPriceEvent(
    portfolioId = portfolio.id,
    portfolioName = portfolio.name,
    securityName = entry.name,
    indicators = indicators.toIndicators(),
    lastPrice = lastPrice,
    priceType = priceType,
    note = entry.note
)

private fun UpdateServiceEvent.UnboundPriceAlert.toNotificationEvent() = UnboundPriceEvent(
    portfolioId = portfolio.id,
    portfolioName = portfolio.name,
    securityName = entry.name,
    indicators = indicators.toIndicators(),
    lastPrice = lastPrice,
    priceType = priceType,
    note = entry.note
)