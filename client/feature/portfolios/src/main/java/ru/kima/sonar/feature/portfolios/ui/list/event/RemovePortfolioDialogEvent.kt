package ru.kima.sonar.feature.portfolios.ui.list.event

internal sealed interface RemovePortfolioDialogUserEvent {
    data object AcceptClicked : RemovePortfolioDialogUserEvent
    data object DismissClicked : RemovePortfolioDialogUserEvent
    data object RefreshClicked : RemovePortfolioDialogUserEvent
}

internal sealed interface RemovePortfolioDialogUiEvent {
    data object PopBack : RemovePortfolioDialogUiEvent
    data object PopBackWithRefresh : RemovePortfolioDialogUiEvent
}