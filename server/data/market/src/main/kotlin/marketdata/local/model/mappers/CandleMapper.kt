package ru.kima.sonar.server.data.market.marketdata.local.model.mappers

import ru.kima.sonar.common.serverapi.model.Candle
import ru.kima.sonar.server.data.market.marketdata.local.model.entities.CandleEntity
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal fun CandleEntity.toCandle() = Candle(
    id = id.value,
    time = time,
    instrumentUid = instrumentUid,
    interval = interval,
    open = open,
    high = high,
    low = low,
    close = close,
    volume = volume,
    isComplete = isComplete,
    lastTrade = lastTrade
)