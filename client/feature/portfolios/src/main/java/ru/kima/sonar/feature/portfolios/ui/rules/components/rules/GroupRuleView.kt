package ru.kima.sonar.feature.portfolios.ui.rules.components.rules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.kima.sonar.common.ui.components.SonarDropdownMenu
import ru.kima.sonar.common.ui.components.SonarDropdownMenuItem
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.CommonDrawables
import ru.kima.sonar.data.homeapi.model.rules.RuleType
import ru.kima.sonar.feature.portfolios.R
import ru.kima.sonar.feature.portfolios.ui.rules.events.RulesAction
import ru.kima.sonar.feature.portfolios.ui.rules.model.DisplayRule


@Composable
internal fun rememberRulesMenu(
    onSelect: (RuleType) -> Unit
): ImmutableList<SonarDropdownMenuItem> = remember(onSelect) {
    persistentListOf(
        SonarDropdownMenuItem.ItemsGroup(
            title = R.string.menu_label_rules_group_group,
            trailingIcon = CommonDrawables.arrow_right_24px,
            children = persistentListOf(
                SonarDropdownMenuItem.SimpleItem(
                    title = R.string.menu_label_rule_type_group,
                    onClick = { onSelect(RuleType.GROUP) }
                )
            )
        ),
        SonarDropdownMenuItem.ItemsGroup(
            title = R.string.menu_label_rules_group_indicators,
            trailingIcon = CommonDrawables.arrow_right_24px,
            children = persistentListOf(
                SonarDropdownMenuItem.SimpleItem(
                    title = R.string.rule_title_rsi,
                    onClick = { onSelect(RuleType.RSI) }
                ),
                SonarDropdownMenuItem.SimpleItem(
                    title = R.string.rule_title_srsi,
                    onClick = { onSelect(RuleType.SRSI) }
                ),
                SonarDropdownMenuItem.SimpleItem(
                    title = R.string.rule_title_mfi,
                    onClick = { onSelect(RuleType.MFI) }
                ),
                SonarDropdownMenuItem.SimpleItem(
                    title = R.string.rule_title_bb,
                    onClick = { onSelect(RuleType.BB) }
                )
            )
        )
    )
}

@Composable
internal fun GroupRuleView(
    group: DisplayRule.Group,
    onAction: (RulesAction) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    titleContent: @Composable (RowScope.() -> Unit)? = null
) {
    RuleCommonView(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.rule_title_group))
                titleContent?.invoke(this)
            }
        },
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var expanded by remember { mutableStateOf(false) }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Button(
                        onClick = {
                            expanded = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enabled
                    ) {
                        Text(stringResource(R.string.button_label_add_rule))

                    }

                    val items = rememberRulesMenu { ruleType ->
                        onAction(RulesAction.AddRule(group.key, ruleType))
                        expanded = false
                    }
                    SonarDropdownMenu(
                        expanded = expanded,
                        items = items,
                        onDismissRequest = { expanded = false }
                    )
                }

                Button(
                    onClick = { onAction(RulesAction.ClearGroup(group.key)) },
                    modifier = Modifier.weight(1f),
                    enabled = enabled
                ) {
                    Text(stringResource(R.string.buttin_label_clear))
                }
            }


            val truthThresholdString =
                remember(group.threshold) { group.threshold.toString() }
            OutlinedTextField(
                value = truthThresholdString,
                onValueChange = {
                    it.toIntOrNull()?.let {
                        onAction(
                            RulesAction.UpdateGroupRuleTruthThreshold(group.key, it)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                label = {
                    Text(stringResource(R.string.label_truth_threshold))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }
    }
}

@Preview(locale = "RU")
@Composable
private fun GroupRulePreview() = SonarPreview {
    GroupRuleView(
        group = DisplayRule.Group(key = 1L, 1, 0, parent = null),
        onAction = {},
        modifier = Modifier.padding(16.dp)
    )
}

//@Preview
//@Composable
//private fun GroupGroupRulePreview() = SonarPreview {
//    val group = GroupRule(
//        truthThreshold = 1,
//        rules = listOf(
//            GroupRule(
//                truthThreshold = 1,
//                rules = listOf(
//                    RsiRule(
//                        requiredCount = 1,
//                        lowThreshold = 30.toBigDecimal(),
//                        highThreshold = 70.toBigDecimal()
//                    ),
//                    SrsiRule(
//                        requiredCount = 1,
//                        lowThreshold = 20.toBigDecimal(),
//                        highThreshold = 80.toBigDecimal()
//                    )
//                )
//            ),
//            GroupRule(
//                truthThreshold = 1,
//                rules = listOf(
//                    MfiRule(
//                        requiredCount = 1,
//                        lowThreshold = 30.toBigDecimal(),
//                        highThreshold = 70.toBigDecimal()
//                    ),
//                    BbRule(
//                        requiredCount = 1,
//                        lowThreshold = 20.toBigDecimal(),
//                        highThreshold = 80.toBigDecimal()
//                    )
//                )
//            )
//        )
//    )
//
//    val display = group.toFlatDisplayRuleList()
//    val asd = display.groupBy { it.key }//.mapValues { it.value.size }
//    Text(display.toString())
//}
