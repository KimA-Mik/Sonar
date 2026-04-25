package ru.kima.sonar.feature.portfolios.ui.addentries.event

import ru.kima.sonar.data.homeapi.error.HomeApiError

internal sealed interface AddEntriesSnackbarMessage {
    data class AddedBulkSecurities(val count: Int) : AddEntriesSnackbarMessage
    data object NoSecuritiesFound : AddEntriesSnackbarMessage
    data class ApiError(val error: HomeApiError) : AddEntriesSnackbarMessage
}