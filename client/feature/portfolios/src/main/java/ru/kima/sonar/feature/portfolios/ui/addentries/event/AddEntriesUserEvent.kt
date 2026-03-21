package ru.kima.sonar.feature.portfolios.ui.addentries.event

internal sealed interface AddEntriesUserEvent {
    data object OpenSelectSecuritiesDialogClicked : AddEntriesUserEvent
    data class ExpandClicked(val uid: String) : AddEntriesUserEvent
    data class UpdateLowPrice(val uid: String, val price: String) : AddEntriesUserEvent
    data class UpdateHighPrice(val uid: String, val price: String) : AddEntriesUserEvent
    data class UpdateTargetDeviation(val uid: String, val deviation: String) : AddEntriesUserEvent
    data class NoteUpdated(val uid: String, val note: String) : AddEntriesUserEvent
    data class RemoveEntryClicked(val uid: String) : AddEntriesUserEvent
    data object SaveChangesClicked : AddEntriesUserEvent
}