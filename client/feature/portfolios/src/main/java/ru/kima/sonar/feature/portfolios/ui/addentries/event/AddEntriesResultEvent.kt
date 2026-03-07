package ru.kima.sonar.feature.portfolios.ui.addentries.event

internal sealed interface AddEntriesResultEvent {
    data object Success : AddEntriesResultEvent
}