package ru.kima.sonar.feature.portfolios.ui.rules.components.rules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import ru.kima.sonar.common.serverapi.model.rules.BbRule
import ru.kima.sonar.common.serverapi.model.rules.GroupRule
import ru.kima.sonar.common.serverapi.model.rules.MfiRule
import ru.kima.sonar.common.serverapi.model.rules.RsiRule
import ru.kima.sonar.common.serverapi.model.rules.SrsiRule
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.feature.portfolios.ui.rules.events.RulesAction
import ru.kima.sonar.feature.portfolios.ui.rules.model.DisplayRule
import ru.kima.sonar.feature.portfolios.ui.rules.model.mapper.toFlatDisplayRuleList

@Composable
internal fun RulesList(
    rules: ImmutableList<DisplayRule>,
    onAction: (RulesAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues.Zero
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(rules, key = { it.key }) { rule ->
            RuleView(
                rule = rule,
                onAction = onAction,
                titleContent = {
                    RuleTitleContent(rule, onAction = onAction)
                },
                modifier = Modifier
                    .padding(start = (rule.depth * 16).dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
internal fun RuleView(
    rule: DisplayRule,
    onAction: (RulesAction) -> Unit,
    modifier: Modifier = Modifier,
    titleContent: @Composable (RowScope.() -> Unit)? = null
) {
    when (rule) {
        is DisplayRule.Indicator -> SimpleIndicatorRuleView(
            rule = rule,
            onAction = onAction,
            modifier = modifier,
            titleContent = titleContent
        )

        is DisplayRule.Group -> GroupRuleView(
            group = rule,
            onAction = onAction,
            modifier = modifier,
            titleContent = titleContent
        )
    }
}

@Composable
internal fun RuleCommonView(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    ProvideTextStyle(MaterialTheme.typography.titleMediumEmphasized) {
        title()
    }
    content()
}

@Preview
@Composable
private fun RuleCommonViewPreview() = SonarPreview {
    RuleCommonView(
        title = { Text("Rule title") },
        content = { Text("Rule content") },
        modifier = Modifier.padding(16.dp)
    )
}

@Preview
@Composable
private fun ListPreview() = SonarPreview {
    val group = GroupRule(
        truthThreshold = 1,
        rules = listOf(
            GroupRule(
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
            GroupRule(
                truthThreshold = 1,
                rules = listOf(
                    MfiRule(
                        requiredCount = 1,
                        lowThreshold = 30.toBigDecimal(),
                        highThreshold = 70.toBigDecimal()
                    ),
                    BbRule(
                        requiredCount = 1,
                        lowThreshold = 20.toBigDecimal(),
                        highThreshold = 80.toBigDecimal()
                    )
                )
            )
        )
    ).toFlatDisplayRuleList().toImmutableList()

    RulesList(
        rules = group,
        onAction = {},
        modifier = Modifier.padding(16.dp)
    )
}