package ru.kima.sonar.feature.portfolios.ui.details.state

import androidx.compose.runtime.Immutable
import java.math.BigDecimal

@Immutable
internal data class EditEntryDialogState(
    val isLoading: Boolean,
    val name: String,
    val price: BigDecimal,
    val lowPrice: String,
    val highPrice: String,
    val note: String,
) {
    companion object {
        fun default(
            isLoading: Boolean = false,
            name: String = "",
            price: BigDecimal = BigDecimal.ZERO,
            lowPrice: String = "",
            highPrice: String = "",
            note: String = ""
        ) = EditEntryDialogState(
            isLoading = isLoading,
            name = name,
            price = price,
            lowPrice = lowPrice,
            highPrice = highPrice,
            note = note
        )
    }
}
