package ru.kima.sonar.feature.portfolios.ui.details.event

internal sealed interface PortfolioDetailsResultEvent {
    data object EntryUpdated : PortfolioDetailsResultEvent
}