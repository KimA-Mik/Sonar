package ru.kima.sonar.common.serverapi.dto.securitieslist.response

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.util.BigDecimalJson
import kotlin.time.Instant

@Serializable
data class ListItemShare(
    val uid: String,
    val ticker: String,
    val name: String,
    val price: BigDecimalJson,
    val priceTimestamp: Instant,
)