package ru.kima.sonar.feature.securities.ui.list

sealed interface SecuritiesListEvent {
    data object RefreshSecurities : SecuritiesListEvent
}