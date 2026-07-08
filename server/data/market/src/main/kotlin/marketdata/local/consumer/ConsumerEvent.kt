package ru.kima.sonar.server.data.market.marketdata.local.consumer

import java.math.BigDecimal
import kotlin.time.Instant

internal data class ConsumerEvent(
    val ticker: String,
    val price: BigDecimal,
    val time: Instant
)