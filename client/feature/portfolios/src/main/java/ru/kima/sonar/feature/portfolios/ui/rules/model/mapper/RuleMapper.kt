package ru.kima.sonar.feature.portfolios.ui.rules.model.mapper

import ru.kima.sonar.common.serverapi.model.rules.BbRule
import ru.kima.sonar.common.serverapi.model.rules.GroupRule
import ru.kima.sonar.common.serverapi.model.rules.MfiRule
import ru.kima.sonar.common.serverapi.model.rules.RsiRule
import ru.kima.sonar.common.serverapi.model.rules.Rule
import ru.kima.sonar.common.serverapi.model.rules.SrsiRule
import ru.kima.sonar.feature.portfolios.ui.rules.model.DisplayRule
import ru.kima.sonar.feature.portfolios.ui.rules.model.ParentRule

/**
 * Maps a hierarchical Rule structure to a flat list of DisplayRule objects.
 * Flattens nested GroupRules while tracking depth for UI indentation.
 */
fun Rule.toFlatDisplayRuleList(): List<DisplayRule> {
    return flattenRules(this, depth = 0, startKey = 1L, parent = null)
}

private fun flattenRules(
    rule: Rule,
    depth: Int,
    startKey: Long,
    parent: ParentRule?
): List<DisplayRule> {
    return when (rule) {
        is GroupRule -> {
            val groupDisplayRule = DisplayRule.Group(
                key = startKey,
                threshold = rule.truthThreshold,
                depth = depth,
                parent = parent
            )
            var key = startKey + 1
            val nestedRules = rule.rules.flatMap {
                val flattend =
                    flattenRules(it, depth + 1, startKey = key, parent = groupDisplayRule)
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
                threshold = rule.requiredCount,
                parent = parent
            )
        )

        is SrsiRule -> listOf(
            DisplayRule.Indicator.Srsi(
                key = startKey,
                depth = depth,
                low = rule.lowThreshold.toFloat(),
                high = rule.highThreshold.toFloat(),
                threshold = rule.requiredCount,
                parent = parent
            )
        )

        is MfiRule -> listOf(
            DisplayRule.Indicator.Mfi(
                key = startKey,
                depth = depth,
                low = rule.lowThreshold.toFloat(),
                high = rule.highThreshold.toFloat(),
                threshold = rule.requiredCount,
                parent = parent
            )
        )

        is BbRule -> listOf(
            DisplayRule.Indicator.Bb(
                key = startKey,
                depth = depth,
                low = rule.lowThreshold.toFloat(),
                high = rule.highThreshold.toFloat(),
                threshold = rule.requiredCount,
                parent = parent
            )
        )
    }
}

