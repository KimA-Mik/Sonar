package ru.kima.sonar.common.serverapi.model

import java.math.BigDecimal
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Candle(
    val id: Long,
    val time: Instant,
    val ticker: String,
    val interval: CandleInterval,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: Long,
    val isComplete: Boolean
)
