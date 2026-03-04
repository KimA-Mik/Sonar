package ru.kima.sonar.feature.portfolios.ui.details.event

internal sealed interface PortfolioDetailsUiEvent {
    data class OpenAddEntriesScreen(val portfolioId: Long) : PortfolioDetailsUiEvent
    data object OpenDeleteEntryDialog : PortfolioDetailsUiEvent
}