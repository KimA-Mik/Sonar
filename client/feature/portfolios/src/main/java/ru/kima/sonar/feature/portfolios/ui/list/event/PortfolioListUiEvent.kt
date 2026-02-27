package ru.kima.sonar.feature.portfolios.ui.list.event

sealed interface PortfolioListUiEvent {
    data object OpenCreatePortfolioDialog : PortfolioListUiEvent
    data object DismissCreatePortfolioDialog : PortfolioListUiEvent
}