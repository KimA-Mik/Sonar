package ru.kima.sonar.feature.portfolios.ui.details.event

internal sealed interface PortfolioDetailsUserEvent {
    data object AddEntriesButtonClicked : PortfolioDetailsUserEvent
    data class EditEntryButtonClicked(val entryUid: String) : PortfolioDetailsUserEvent
    data class DeleteEntryButtonClicked(val entryUid: String) : PortfolioDetailsUserEvent
    data object Refresh : PortfolioDetailsUserEvent
}