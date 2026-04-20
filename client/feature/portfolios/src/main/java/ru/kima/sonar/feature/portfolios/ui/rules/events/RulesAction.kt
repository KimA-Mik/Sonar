package ru.kima.sonar.feature.portfolios.ui.rules.events

import ru.kima.sonar.data.homeapi.model.rules.RuleType

internal sealed interface RulesAction {
    val key: Long

    data class UpdateRsiRuleAction(
        override val key: Long,
        val requiredCount: Int,
        val lowThreshold: Float,
        val highThreshold: Float
    ) : RulesAction

    data class UpdateSrsiRuleAction(
        override val key: Long,
        val requiredCount: Int,
        val lowThreshold: Float,
        val highThreshold: Float
    ) : RulesAction

    data class UpdateMfiRuleAction(
        override val key: Long,
        val requiredCount: Int,
        val lowThreshold: Float,
        val highThreshold: Float
    ) : RulesAction

    data class UpdateBbRuleAction(
        override val key: Long,
        val requiredCount: Int,
        val lowThreshold: Float,
        val highThreshold: Float
    ) : RulesAction

    data class UpdateGroupRuleTruthThreshold(
        override val key: Long,
        val truthThreshold: Int
    ) : RulesAction

    data class DeleteRule(
        override val key: Long
    ) : RulesAction

    data class AddRule(
        override val key: Long,
        val ruleType: RuleType
    ) : RulesAction

    data class ClearGroup(
        override val key: Long
    ) : RulesAction
}