package ru.kima.sonar.common.serverapi.dto.portfolio.request

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.model.rules.Rule
import ru.kima.sonar.common.serverapi.model.rules.RulesMode

@Serializable
data class UpdateRuleRequest(
    val mode: RulesMode,
    val rule: Rule
)
