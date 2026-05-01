package ru.kima.sonar.feature.portfolios.ui.components.editentry

import androidx.compose.runtime.Immutable
import java.math.BigDecimal

@Immutable
internal sealed interface EditEntryComponent {
    val key: String

    @Immutable
    data class Title(
        override val key: String,
        val title: String,
        val price: BigDecimal,
        val targetDeviation: String,
    ) : EditEntryComponent

    @Immutable
    data class StopLoss(
        override val key: String,
        val index: Int,
        val price: String,
        val note: String
    ) : EditEntryComponent

    @Immutable
    data class TakeProfit(
        override val key: String,
        val index: Int,
        val price: String,
        val note: String
    ) : EditEntryComponent

    @Immutable
    data class AddStopLoss(override val key: String) : EditEntryComponent

    @Immutable
    data class AddTakeProfit(override val key: String) : EditEntryComponent

    @Immutable
    data class Padding(override val key: String) : EditEntryComponent
}