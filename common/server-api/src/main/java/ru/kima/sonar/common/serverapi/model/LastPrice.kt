package ru.kima.sonar.common.serverapi.model

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.model.schema.LastPriceType
import ru.kima.sonar.common.serverapi.util.BigDecimalJson
import kotlin.time.Instant

@Serializable
data class LastPrice(
    val uid: String,
    val price: BigDecimalJson,
    val time: Instant,
    val lastPriceType: LastPriceType
)