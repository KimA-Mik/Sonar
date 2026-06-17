package ru.kima.sonar.server.data.market.marketdata.local.model.mappers

import ru.kima.sonar.common.serverapi.model.Candle
import ru.kima.sonar.server.data.market.marketdata.local.model.entities.CandleEntity
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal fun CandleEntity.toCandle() = Candle(
    id = id.value,
    time = Instant.fromEpochSeconds(timestamp),
    ticker = ticker,
    interval = interval,
    open = open,
    high = high,
    low = low,
    close = close,
    volume = volume,
    isComplete = isComplete,
)

internal fun CandleEntity.putInside(domainObject: Candle) {
    ticker = domainObject.ticker
    timestamp = domainObject.time.epochSeconds
    interval = domainObject.interval
    open = domainObject.open
    high = domainObject.high
    low = domainObject.low
    close = domainObject.close
    volume = domainObject.volume
    isComplete = domainObject.isComplete
}