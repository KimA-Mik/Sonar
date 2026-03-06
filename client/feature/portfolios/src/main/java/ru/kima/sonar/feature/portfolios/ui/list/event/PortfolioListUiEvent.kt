package ru.kima.sonar.feature.portfolios.ui.list.event

sealed interface PortfolioListUiEvent {
    data object OpenCreatePortfolioDialog : PortfolioListUiEvent
    data object OpenRenamePortfolioDialog : PortfolioListUiEvent
    data class OpenDeletePortfolioDialog(val portfolioId: Long) : PortfolioListUiEvent
    data object DismissDialog : PortfolioListUiEvent
    data class NavigateToPortfolioDetails(val portfolioId: Long) : PortfolioListUiEvent
}