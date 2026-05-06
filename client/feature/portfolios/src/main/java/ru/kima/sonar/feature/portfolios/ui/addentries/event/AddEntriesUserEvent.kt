package ru.kima.sonar.feature.portfolios.ui.addentries.event

internal sealed interface AddEntriesUserEvent {
    data object OpenSelectSecuritiesDialogClicked : AddEntriesUserEvent
    data object SaveChangesClicked : AddEntriesUserEvent
    data class DeleteEntry(val uid: String) : AddEntriesUserEvent
    data class AddStopLoss(val uid: String) : AddEntriesUserEvent
    data class AddTakeProfit(val uid: String) : AddEntriesUserEvent
    data class UpdateStopLossPrice(val key: String, val price: String) : AddEntriesUserEvent
    data class UpdateStopLossNote(val key: String, val note: String) : AddEntriesUserEvent
    data class DeleteStopLoss(val key: String) : AddEntriesUserEvent
    data class UpdateTakeProfitPrice(val key: String, val price: String) : AddEntriesUserEvent
    data class UpdateTakeProfitNote(val key: String, val note: String) : AddEntriesUserEvent
    data class DeleteTakeProfit(val key: String) : AddEntriesUserEvent
}