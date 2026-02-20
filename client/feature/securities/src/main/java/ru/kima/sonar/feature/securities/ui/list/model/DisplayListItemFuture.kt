package ru.kima.sonar.feature.securities.ui.list.model

import androidx.compose.runtime.Immutable
import java.math.BigDecimal
import kotlin.time.Instant

@Immutable
data class DisplayListItemFuture(
    val uid: String,
    val name: String,
    val ticker: String,
    val price: BigDecimal,
    val expirationDate: Instant
)
