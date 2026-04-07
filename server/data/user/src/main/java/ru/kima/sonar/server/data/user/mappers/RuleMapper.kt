package ru.kima.sonar.server.data.user.mappers

import ru.kima.sonar.server.data.user.model.portfolio.LightPortfolioWithRule
import ru.kima.sonar.server.data.user.model.portfolio.PortfolioRule
import ru.kima.sonar.server.data.user.scema.portfolio.PortfolioEntity
import ru.kima.sonar.server.data.user.scema.portfolio.RulesEntity

internal fun PortfolioEntity.toLightPortfolioWithRule(rule: PortfolioRule) = LightPortfolioWithRule(
    id = id.value,
    userId = userId,
    name = name,
    rule = rule
)

internal fun RulesEntity.toDomainModel() = PortfolioRule(
    id = id.value,
    portfolioId = portfolioId,
    mode = rulesMode,
    rule = rule
)

internal fun RulesEntity.pitInside(portfolioRule: PortfolioRule) {
    rulesMode = portfolioRule.mode
    rule = portfolioRule.rule
}