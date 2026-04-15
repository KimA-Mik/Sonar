package ru.kima.sonar.feature.portfolios.ui.rules.components.rules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.kima.sonar.common.serverapi.model.rules.BbRule
import ru.kima.sonar.common.serverapi.model.rules.MfiRule
import ru.kima.sonar.common.serverapi.model.rules.RsiRule
import ru.kima.sonar.common.serverapi.model.rules.SimpleIndicatorRule
import ru.kima.sonar.common.serverapi.model.rules.SrsiRule
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.LocalNumberFormat
import ru.kima.sonar.feature.portfolios.R
import java.math.BigDecimal

//val requiredCount: Int
//val lowThreshold: BigDecimalJson
//val highThreshold: BigDecimalJson

@Composable
internal fun SimpleIndicatorRuleView(
    rule: SimpleIndicatorRule,
    onAction: (RulesAction) -> Unit,
    modifier: Modifier = Modifier,
    depth: Int = 0,
    titleContent: @Composable (RowScope.() -> Unit)? = null
) {
    IndicatorRuleBody(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val id = when (rule) {
                    is BbRule -> R.string.rule_title_bb
                    is MfiRule -> R.string.rule_title_mfi
                    is RsiRule -> R.string.rule_title_rsi
                    is SrsiRule -> R.string.rule_title_srsi
                }
                Text(stringResource(id))
                titleContent?.let {
                    it()
                }
            }
        },
        value = rule.lowThreshold.toFloat()..rule.highThreshold.toFloat(),
        onValueChange = { range ->
            val action = when (rule) {
                is BbRule -> RulesAction.UpdateBbRuleAction(
                    requiredCount = rule.requiredCount,
                    lowThreshold = BigDecimal(range.start.toDouble()),
                    highThreshold = BigDecimal(range.endInclusive.toDouble())
                )

                is MfiRule -> RulesAction.UpdateMfiRuleAction(
                    requiredCount = rule.requiredCount,
                    lowThreshold = BigDecimal(range.start.toDouble()),
                    highThreshold = BigDecimal(range.endInclusive.toDouble())
                )

                is RsiRule -> RulesAction.UpdateRsiRuleAction(
                    requiredCount = rule.requiredCount,
                    lowThreshold = BigDecimal(range.start.toDouble()),
                    highThreshold = BigDecimal(range.endInclusive.toDouble())
                )

                is SrsiRule -> RulesAction.UpdateSrsiRuleAction(
                    requiredCount = rule.requiredCount,
                    lowThreshold = BigDecimal(range.start.toDouble()),
                    highThreshold = BigDecimal(range.endInclusive.toDouble())
                )
            }
            onAction(action)
        },
        timeframes = rule.requiredCount,
        onTimeframesChange = { timeframes ->
            val count = timeframes.toIntOrNull() ?: return@IndicatorRuleBody
            val action = when (rule) {
                is BbRule -> RulesAction.UpdateBbRuleAction(
                    requiredCount = count,
                    lowThreshold = rule.lowThreshold,
                    highThreshold = rule.highThreshold
                )

                is MfiRule -> RulesAction.UpdateMfiRuleAction(
                    requiredCount = count,
                    lowThreshold = rule.lowThreshold,
                    highThreshold = rule.highThreshold
                )

                is RsiRule -> RulesAction.UpdateRsiRuleAction(
                    requiredCount = count,
                    lowThreshold = rule.lowThreshold,
                    highThreshold = rule.highThreshold
                )

                is SrsiRule -> RulesAction.UpdateSrsiRuleAction(
                    requiredCount = count,
                    lowThreshold = rule.lowThreshold,
                    highThreshold = rule.highThreshold
                )
            }
            onAction(action)
        },
        modifier = modifier,
    )
}

@Composable
private fun IndicatorRuleBody(
    title: @Composable () -> Unit,
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    timeframes: Int,
    onTimeframesChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    valueRang: ClosedFloatingPointRange<Float> = 0f..100f,
    valueFormatter: @Composable (Float) -> String = {
        val numberFormat = LocalNumberFormat.current
        numberFormat.format(it)
    }
) {
    RuleCommonView(
        title = title,
        modifier = modifier.width(IntrinsicSize.Min)
    ) {
        val timeframesString = remember(timeframes) { timeframes.toString() }
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = timeframesString,
                onValueChange = onTimeframesChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                label = {
                    Text(stringResource(R.string.label_timeframes))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            RangeSlider(
                value = value,
                onValueChange = { range -> onValueChange(range) },
                valueRange = valueRang
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(valueFormatter(value.start))
                Text(valueFormatter(value.endInclusive))
            }
        }
    }
}

@Preview
@Composable
private fun SimpleIndicatorRulePreview() = SonarPreview {
    SimpleIndicatorRuleView(
        rule = RsiRule(
            requiredCount = 1,
            lowThreshold = BigDecimal(24),
            highThreshold = BigDecimal(69)
        ),
        onAction = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    )
}