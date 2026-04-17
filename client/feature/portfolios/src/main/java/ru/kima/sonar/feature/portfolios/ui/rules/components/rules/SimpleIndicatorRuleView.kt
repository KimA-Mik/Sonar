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
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.LocalNumberFormat
import ru.kima.sonar.feature.portfolios.R
import ru.kima.sonar.feature.portfolios.ui.rules.model.DisplayRule

//val requiredCount: Int
//val lowThreshold: BigDecimalJson
//val highThreshold: BigDecimalJson

@Composable
internal fun SimpleIndicatorRuleView(
    rule: DisplayRule.Indicator,
    onAction: (RulesAction) -> Unit,
    modifier: Modifier = Modifier,
    titleContent: @Composable (RowScope.() -> Unit)? = null
) {
    IndicatorRuleBody(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val id = when (rule) {
                    is DisplayRule.Indicator.Bb -> R.string.rule_title_bb
                    is DisplayRule.Indicator.Mfi -> R.string.rule_title_mfi
                    is DisplayRule.Indicator.Rsi -> R.string.rule_title_rsi
                    is DisplayRule.Indicator.Srsi -> R.string.rule_title_srsi
                }
                Text(stringResource(id))
                titleContent?.let {
                    it()
                }
            }
        },
        value = rule.low..rule.high,
        onValueChange = { range ->
            val action = when (rule) {
                is DisplayRule.Indicator.Bb -> RulesAction.UpdateBbRuleAction(
                    key = rule.key,
                    requiredCount = rule.threshold,
                    lowThreshold = range.start,
                    highThreshold = range.endInclusive
                )

                is DisplayRule.Indicator.Mfi -> RulesAction.UpdateMfiRuleAction(
                    key = rule.key,
                    requiredCount = rule.threshold,
                    lowThreshold = range.start,
                    highThreshold = range.endInclusive
                )

                is DisplayRule.Indicator.Rsi -> RulesAction.UpdateRsiRuleAction(
                    key = rule.key,
                    requiredCount = rule.threshold,
                    lowThreshold = range.start,
                    highThreshold = range.endInclusive
                )

                is DisplayRule.Indicator.Srsi -> RulesAction.UpdateSrsiRuleAction(
                    key = rule.key,
                    requiredCount = rule.threshold,
                    lowThreshold = range.start,
                    highThreshold = range.endInclusive
                )
            }
            onAction(action)
        },
        timeframes = rule.threshold,
        onTimeframesChange = { timeframes ->
            val count = timeframes.toIntOrNull() ?: return@IndicatorRuleBody
            val action = when (rule) {
                is DisplayRule.Indicator.Bb -> RulesAction.UpdateBbRuleAction(
                    key = rule.key,
                    requiredCount = count,
                    lowThreshold = rule.low,
                    highThreshold = rule.high
                )

                is DisplayRule.Indicator.Mfi -> RulesAction.UpdateMfiRuleAction(
                    key = rule.key,
                    requiredCount = count,
                    lowThreshold = rule.low,
                    highThreshold = rule.high
                )

                is DisplayRule.Indicator.Rsi -> RulesAction.UpdateRsiRuleAction(
                    key = rule.key,
                    requiredCount = count,
                    lowThreshold = rule.low,
                    highThreshold = rule.high
                )

                is DisplayRule.Indicator.Srsi -> RulesAction.UpdateSrsiRuleAction(
                    key = rule.key,
                    requiredCount = count,
                    lowThreshold = rule.low,
                    highThreshold = rule.high
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
        rule = DisplayRule.Indicator.Rsi(
            key = 0L,
            depth = 0,
            threshold = 1,
            low = 24f,
            high = 69f,
            parent = null
        ),
        onAction = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    )
}