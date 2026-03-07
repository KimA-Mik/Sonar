package ru.kima.sonar.feature.portfolios.ui.details.model

import androidx.compose.runtime.Immutable
import java.math.BigDecimal

@Immutable
internal data class DisplayItemEntry(
    val id: Long,
    val uid: String,
    val name: String,
    val price: BigDecimal,
    val lowPrice: BigDecimal,
    val highPrice: BigDecimal,
    val note: String,
    val showNote: Boolean
)
