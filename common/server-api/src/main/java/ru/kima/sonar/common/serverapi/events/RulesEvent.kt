package ru.kima.sonar.common.serverapi.events

import ru.kima.sonar.common.serverapi.events.model.Indicators
import ru.kima.sonar.common.serverapi.model.LastPrice
import ru.kima.sonar.common.serverapi.model.portfolio.SecurityType

data class RulesEvent(
    val portfolioId: Long,
    val portfolioName: String,
    val ticker: String,
    val securityType: SecurityType,
    val securityName: String,
    val indicators: Indicators,
    val lastPrice: LastPrice,
    val note: String
) : NotificationEvent
