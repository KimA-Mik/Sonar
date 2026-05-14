package ru.kima.sonar.feature.portfolios.ui.details.event

internal sealed interface EditEntryUserEvent {
    data object ApplyChangesClicked : EditEntryUserEvent
    data class StopLossPriceChange(val key: String, val price: String) : EditEntryUserEvent
    data class StopLossNoteChange(val key: String, val note: String) : EditEntryUserEvent
    data class DeleteStopLoss(val key: String) : EditEntryUserEvent
    data class TakeProfitPriceChange(val key: String, val price: String) : EditEntryUserEvent
    data class TakeProfitNoteChange(val key: String, val note: String) : EditEntryUserEvent
    data class DeleteTakeProfit(val key: String) : EditEntryUserEvent
    data object AddStopLoss : EditEntryUserEvent
    data object AddTakeProfit : EditEntryUserEvent
}