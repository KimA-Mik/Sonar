package ru.kima.sonar.server.feature.portfolios.mappers

import ru.kima.sonar.common.serverapi.model.portfolio.RuleEditPortfolio
import ru.kima.sonar.common.serverapi.model.portfolio.SonarRule
import ru.kima.sonar.server.data.user.model.portfolio.LightPortfolioWithRule
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioRule

fun LightPortfolioWithRule.toDto() = RuleEditPortfolio(
    id = id,
    name = name,
    rule = rule.toDto()
)

fun PortfolioRule.toDto() = SonarRule(
    id = id,
    mode = mode,
    rule = rule
)