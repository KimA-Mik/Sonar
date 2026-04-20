package ru.kima.sonar.feature.portfolios.ui.rules.events

import ru.kima.sonar.common.serverapi.model.rules.RulesMode
import ru.kima.sonar.data.homeapi.model.rules.RuleType

internal sealed interface RulesScreenUserEvent {
    data class SetMode(val mode: RulesMode) : RulesScreenUserEvent
    data class SetRootRule(val ruleType: RuleType) : RulesScreenUserEvent
    data class RuleAction(val action: RulesAction) : RulesScreenUserEvent
}