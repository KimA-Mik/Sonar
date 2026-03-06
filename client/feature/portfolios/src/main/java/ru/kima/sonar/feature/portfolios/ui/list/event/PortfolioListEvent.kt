package ru.kima.sonar.feature.portfolios.ui.list.event

internal sealed interface PortfolioListEvent {
    data object CreatePortfolioClicked : PortfolioListEvent
    data object Refresh : PortfolioListEvent
    data class UpdatePortfolioName(val name: String) : PortfolioListEvent
    data object AcceptNewPortfolioDialog : PortfolioListEvent
    data object DismissNewPortfolioDialog : PortfolioListEvent
    data class RenamePortfolioClicked(val portfolioId: Long) : PortfolioListEvent
    data class DeletePortfolioClicked(val portfolioId: Long) : PortfolioListEvent
    data object AcceptRenamePortfolioDialog : PortfolioListEvent
    data object DismissRenamePortfolioDialog : PortfolioListEvent
    data class PortfolioClicked(val portfolioId: Long) : PortfolioListEvent
}