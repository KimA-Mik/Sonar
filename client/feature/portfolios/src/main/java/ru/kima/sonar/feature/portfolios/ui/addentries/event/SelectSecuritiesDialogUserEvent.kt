package ru.kima.sonar.feature.portfolios.ui.addentries.event

internal sealed interface SelectSecuritiesDialogUserEvent {
    data class QueryUpdated(val query: String) : SelectSecuritiesDialogUserEvent
    data class EntryChecked(val uid: String) : SelectSecuritiesDialogUserEvent
    data object RefreshRequest : SelectSecuritiesDialogUserEvent
}