package ru.kima.sonar.feature.portfolios.ui.details.event

internal sealed interface EditEntryUserEvent {
    data class LowPriceUpdated(val lowPrice: String) : EditEntryUserEvent
    data class HighPriceUpdated(val highPrice: String) : EditEntryUserEvent
    data class NoteUpdated(val note: String) : EditEntryUserEvent
    data object ApplyChangesClicked : EditEntryUserEvent
}