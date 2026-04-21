package ru.kima.sonar.common.serverapi.model.portfolio

import kotlinx.serialization.Serializable
import ru.kima.sonar.common.serverapi.model.rules.Rule
import ru.kima.sonar.common.serverapi.model.rules.RulesMode

@Serializable
data class SonarRule(
    val id: Long,
    val mode: RulesMode,
    val rule: Rule
)