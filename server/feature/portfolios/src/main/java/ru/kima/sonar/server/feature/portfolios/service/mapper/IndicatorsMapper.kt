package ru.kima.sonar.server.feature.portfolios.service.mapper

import ru.kima.sonar.common.serverapi.events.model.Indicators
import ru.kima.sonar.server.feature.portfolios.service.model.CacheEntry

fun CacheEntry.toIndicators() = Indicators(
    min15Rsi = min15Rsi,
    hourlyRsi = hourlyRsi,
    hour4Rsi = hour4Rsi,
    dailyRsi = dailyRsi,
    min15bb = min15bb.toEventData(),
    hourlyBb = hourlyBb.toEventData(),
    hour4Bb = hour4Bb.toEventData(),
    dailyBb = dailyBb.toEventData(),
    min15Mfi = min15Mfi,
    hourlyMfi = hourlyMfi,
    hour4Mfi = hour4Mfi,
    dailyMfi = dailyMfi,
    min15Srsi = min15Srsi,
    hourlySrsi = hourlySrsi,
    hour4Srsi = hour4Srsi,
    dailySrsi = dailySrsi
)