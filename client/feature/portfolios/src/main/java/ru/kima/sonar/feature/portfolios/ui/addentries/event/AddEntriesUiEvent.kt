package ru.kima.sonar.feature.portfolios.ui.addentries.event

internal sealed interface AddEntriesUiEvent {
    data object OpenSelectSecuritiesDialog : AddEntriesUiEvent
    data object PopBackSuccess : AddEntriesUiEvent
}