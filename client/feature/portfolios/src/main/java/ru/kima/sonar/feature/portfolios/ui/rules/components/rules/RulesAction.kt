package ru.kima.sonar.feature.portfolios.ui.rules.components.rules

import java.math.BigDecimal

internal sealed interface RulesAction {
    data class UpdateRsiRuleAction(
        val requiredCount: Int,
        val lowThreshold: BigDecimal,
        val highThreshold: BigDecimal
    ) : RulesAction

    data class UpdateSrsiRuleAction(
        val requiredCount: Int,
        val lowThreshold: BigDecimal,
        val highThreshold: BigDecimal
    ) : RulesAction

    data class UpdateMfiRuleAction(
        val requiredCount: Int,
        val lowThreshold: BigDecimal,
        val highThreshold: BigDecimal
    ) : RulesAction

    data class UpdateBbRuleAction(
        val requiredCount: Int,
        val lowThreshold: BigDecimal,
        val highThreshold: BigDecimal
    ) : RulesAction

    data class GroupRuleAction(
        val index: Int,
        val action: RulesAction
    ) : RulesAction

    data class UpdateGroupRuleTruthThreshold(val truthThreshold: Int) : RulesAction
}
