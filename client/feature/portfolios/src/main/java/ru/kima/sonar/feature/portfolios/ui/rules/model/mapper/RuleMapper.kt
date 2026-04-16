package ru.kima.sonar.feature.portfolios.ui.rules.model.mapper

import ru.kima.sonar.common.serverapi.model.rules.BbRule
import ru.kima.sonar.common.serverapi.model.rules.GroupRule
import ru.kima.sonar.common.serverapi.model.rules.MfiRule
import ru.kima.sonar.common.serverapi.model.rules.RsiRule
import ru.kima.sonar.common.serverapi.model.rules.Rule
import ru.kima.sonar.common.serverapi.model.rules.SrsiRule
import ru.kima.sonar.feature.portfolios.ui.rules.model.DisplayRule

/**
 * Maps a hierarchical Rule structure to a flat list of DisplayRule objects.
 * Flattens nested GroupRules while tracking depth for UI indentation.
 */
fun Rule.toFlatDisplayRuleList(): List<DisplayRule> {
    return flattenRules(this, depth = 0, startKey = 1L)
}

private fun flattenRules(rule: Rule, depth: Int, startKey: Long): List<DisplayRule> {
    return when (rule) {
        is GroupRule -> {
            val groupDisplayRule = DisplayRule.Group(
                key = startKey,
                threshold = rule.truthThreshold,
                depth = depth
            )
            var key = startKey + 1
            val nestedRules = rule.rules.flatMap {
                val flattend = flattenRules(it, depth + 1, startKey = key)
                key = (flattend.lastOrNull()?.key ?: key) + 1
                flattend
            }
            listOf(groupDisplayRule) + nestedRules
        }

        is RsiRule -> listOf(
            DisplayRule.Indicator.Rsi(
                key = startKey,
                depth = depth,
                low = rule.lowThreshold.toFloat(),
                high = rule.highThreshold.toFloat(),
                threshold = rule.requiredCount
            )
        )

        is SrsiRule -> listOf(
            DisplayRule.Indicator.Srsi(
                key = startKey,
                depth = depth,
                low = rule.lowThreshold.toFloat(),
                high = rule.highThreshold.toFloat(),
                threshold = rule.requiredCount
            )
        )

        is MfiRule -> listOf(
            DisplayRule.Indicator.Mfi(
                key = startKey,
                depth = depth,
                low = rule.lowThreshold.toFloat(),
                high = rule.highThreshold.toFloat(),
                threshold = rule.requiredCount
            )
        )

        is BbRule -> listOf(
            DisplayRule.Indicator.Bb(
                key = startKey,
                depth = depth,
                low = rule.lowThreshold.toFloat(),
                high = rule.highThreshold.toFloat(),
                threshold = rule.requiredCount
            )
        )
    }
}

