package ru.kima.sonar.feature.portfolios.ui.rules.components.rules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.kima.sonar.common.serverapi.model.rules.GroupRule
import ru.kima.sonar.common.serverapi.model.rules.RsiRule
import ru.kima.sonar.common.serverapi.model.rules.SrsiRule
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.feature.portfolios.R

@Composable
internal fun GroupRuleView(
    group: GroupRule,
    onAction: (RulesAction) -> Unit,
    modifier: Modifier = Modifier,
    depth: Int = 0,
    titleContent: @Composable (RowScope.() -> Unit)? = null
) {
    RuleCommonView(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
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
            val truthThresholdString =
                remember(group.truthThreshold) { group.truthThreshold.toString() }
            OutlinedTextField(
                value = truthThresholdString,
                onValueChange = {
                    it.toIntOrNull()?.let {
                        onAction(
                            RulesAction.UpdateGroupRuleTruthThreshold(it)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(R.string.label_truth_threshold))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = depth == 0
            ) {
                items(group.rules.size) { index ->
                    RuleView(
                        rule = group.rules[index],
                        onAction = { action ->
                            onAction(
                                RulesAction.GroupRuleAction(
                                    index,
                                    action
                                )
                            )
                        },
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .fillMaxWidth(),
                        depth = depth + 1
                    )
                }
            }
        }
    }
}

@Preview(locale = "RU")
@Composable
private fun GroupRulePreview() = SonarPreview {
    GroupRuleView(
        group = GroupRule(
            truthThreshold = 1,
            rules = listOf(
                RsiRule(
                    requiredCount = 1,
                    lowThreshold = 30.toBigDecimal(),
                    highThreshold = 70.toBigDecimal()
                ),
                SrsiRule(
                    requiredCount = 1,
                    lowThreshold = 20.toBigDecimal(),
                    highThreshold = 80.toBigDecimal()
                )
            )
        ),
        onAction = {},
        modifier = Modifier.padding(16.dp)
    )
}

//@Preview
//@Composable
//private fun GroupGroupRulePreview() = SonarPreview {
//    GroupRuleView(
//        group = GroupRule(
//            truthThreshold = 1,
//            rules = listOf(
//                GroupRule(
//                    truthThreshold = 1,
//                    rules = listOf(
//                        RsiRule(
//                            requiredCount = 1,
//                            lowThreshold = 30.toBigDecimal(),
//                            highThreshold = 70.toBigDecimal()
//                        ),
//                        SrsiRule(
//                            requiredCount = 1,
//                            lowThreshold = 20.toBigDecimal(),
//                            highThreshold = 80.toBigDecimal()
//                        )
//                    )
//                ),
//                GroupRule(
//                    truthThreshold = 1,
//                    rules = listOf(
//                        RsiRule(
//                            requiredCount = 1,
//                            lowThreshold = 30.toBigDecimal(),
//                            highThreshold = 70.toBigDecimal()
//                        ),
//                        SrsiRule(
//                            requiredCount = 1,
//                            lowThreshold = 20.toBigDecimal(),
//                            highThreshold = 80.toBigDecimal()
//                        )
//                    )
//                )
//            )
//        ),
//        onAction = {},
//        modifier = Modifier.padding(16.dp)
//    )
//}
