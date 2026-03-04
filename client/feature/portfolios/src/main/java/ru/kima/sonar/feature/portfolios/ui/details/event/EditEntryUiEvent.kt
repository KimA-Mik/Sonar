package ru.kima.sonar.feature.portfolios.ui.details.event

internal sealed interface EditEntryUiEvent {
    data object Success : EditEntryUiEvent
}