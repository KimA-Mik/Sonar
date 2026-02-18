package ru.kima.sonar.common.serverapi.serverresponse.securitieslist

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.util.BigDecimalJson
import kotlin.time.Instant

@Serializable
data class ListItemFuture(
    val uid: String,
    val ticker: String,
    val name: String,
    val price: BigDecimalJson,
    val priceTimestamp: Instant,
    val expirationDate: Instant,
    val basicAsset: String
)