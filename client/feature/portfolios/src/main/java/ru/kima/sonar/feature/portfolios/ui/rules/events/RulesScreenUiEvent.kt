package ru.kima.sonar.feature.portfolios.ui.rules.events

import ru.kima.sonar.data.homeapi.error.HomeApiError
import ru.kima.sonar.data.homeapi.model.rules.RuleType

internal sealed interface RulesScreenUiEvent {
    data class ShowClearGroupDialog(val key: Long) : RulesScreenUiEvent
    data class ShowDeleteRuleDialog(val key: Long, val ruleType: RuleType) : RulesScreenUiEvent
    data object SuccessfullySaved : RulesScreenUiEvent
    data class ErrorSaving(val error: HomeApiError) : RulesScreenUiEvent
}