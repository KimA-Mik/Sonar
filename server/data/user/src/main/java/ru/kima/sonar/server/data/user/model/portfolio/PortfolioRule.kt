package ru.kima.sonar.server.data.user.model.portfolio

import ru.kima.sonar.common.serverapi.model.rules.Rule
import ru.kima.sonar.common.serverapi.model.rules.RulesMode

data class PortfolioRule(
    val id: Long,
    val portfolioId: Long,
    val mode: RulesMode,
    val rule: Rule
)
