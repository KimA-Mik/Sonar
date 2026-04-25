package ru.kima.sonar.feature.portfolios.ui.addentries.event

internal sealed interface AddEntriesSnackbarMessage {
    data class AddedBulkSecurities(val count: Int) : AddEntriesSnackbarMessage
    data object NoSecuritiesFound : AddEntriesSnackbarMessage
}