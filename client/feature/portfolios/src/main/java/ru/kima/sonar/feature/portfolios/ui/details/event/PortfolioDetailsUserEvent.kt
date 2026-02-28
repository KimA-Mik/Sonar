package ru.kima.sonar.feature.portfolios.ui.details.event

internal sealed interface PortfolioDetailsUserEvent {
    data object AddEntriesButtonClicked : PortfolioDetailsUserEvent
}