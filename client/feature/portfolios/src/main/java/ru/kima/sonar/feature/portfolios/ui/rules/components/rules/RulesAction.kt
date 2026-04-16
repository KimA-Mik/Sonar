package ru.kima.sonar.feature.portfolios.ui.rules.components.rules

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
}
