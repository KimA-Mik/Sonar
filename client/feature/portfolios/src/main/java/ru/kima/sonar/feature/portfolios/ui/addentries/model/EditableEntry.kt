package ru.kima.sonar.feature.portfolios.ui.addentries.model

import androidx.compose.runtime.Immutable
import java.math.BigDecimal

@Immutable
internal data class EditableEntry(
    val uid: String,
    val ticker: String,
    val price: BigDecimal,
    val lowPrice: String,
    val lowPriceError: Boolean,
    val highPrice: String,
    val highPriceError: Boolean,
    val expanded: Boolean,
    val note: String
)
