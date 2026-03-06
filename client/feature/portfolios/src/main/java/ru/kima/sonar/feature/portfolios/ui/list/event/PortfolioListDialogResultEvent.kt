package ru.kima.sonar.feature.portfolios.ui.list.event

internal sealed interface PortfolioListDialogResultEvent {
    data object Success : PortfolioListDialogResultEvent
}