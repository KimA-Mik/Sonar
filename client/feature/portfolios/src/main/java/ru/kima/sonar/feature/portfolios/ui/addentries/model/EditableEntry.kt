package ru.kima.sonar.feature.portfolios.ui.addentries.model

import androidx.compose.runtime.Immutable
import java.math.BigDecimal

@Immutable
internal data class EditableEntry(
    val uid: String,
    val ticker: String,
    val price: BigDecimal,
    val lowPrice: BigDecimal,
    val highPrice: BigDecimal,
    val expanded: Boolean,
    val note: String
)
