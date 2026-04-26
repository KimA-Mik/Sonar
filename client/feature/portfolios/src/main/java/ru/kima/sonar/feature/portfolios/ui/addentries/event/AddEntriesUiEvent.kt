package ru.kima.sonar.feature.portfolios.ui.addentries.event

internal sealed interface AddEntriesUiEvent {
    data object OpenSelectSecuritiesDialog : AddEntriesUiEvent
    data object PopBackSuccess : AddEntriesUiEvent
    data class ShowSnackbar(val snackbarMessage: AddEntriesSnackbarMessage) : AddEntriesUiEvent
}