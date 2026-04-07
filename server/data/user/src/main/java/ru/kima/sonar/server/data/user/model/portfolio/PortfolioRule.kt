package ru.kima.sonar.server.data.user.model.portfolio

import ru.kima.sonar.common.serverapi.model.rules.GroupRule
import ru.kima.sonar.common.serverapi.model.rules.Rule
import ru.kima.sonar.common.serverapi.model.rules.RulesMode

data class PortfolioRule(
    val id: Long,
    val portfolioId: Long,
    val mode: RulesMode,
    val rule: Rule
) {
    companion object {
        fun default(
            portfolioId: Long = 0,
            id: Long = 0L,
            mode: RulesMode = RulesMode.LIMIT_SECURITIES,
            rule: Rule = GroupRule(1, emptyList())
        ) = PortfolioRule(
            id = 0L,
            portfolioId = portfolioId,
            mode = RulesMode.RULES_DISABLED,
            rule = rule
        )
    }
}
