package ru.kima.sonar.feature.portfolios.ui.rules.state

import androidx.compose.runtime.Immutable

@Immutable
sealed interface RulesLoadingStatus {
    object Loading : RulesLoadingStatus
    data class Error(val message: String) : RulesLoadingStatus
    object Success : RulesLoadingStatus
}