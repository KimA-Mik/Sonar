package ru.kima.sonar.common.serverapi.model

import ru.kima.sonar.common.serverapi.model.schema.LastPriceType
import java.math.BigDecimal
import kotlin.time.Instant

data class LastPrice(
    val uid: String,
    val price: BigDecimal,
    val time: Instant,
    val lastPriceType: LastPriceType
)