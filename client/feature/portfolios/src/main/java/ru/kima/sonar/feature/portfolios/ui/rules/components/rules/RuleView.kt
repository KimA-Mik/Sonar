package ru.kima.sonar.feature.portfolios.ui.rules.components.rules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.kima.sonar.common.serverapi.model.rules.GroupRule
import ru.kima.sonar.common.serverapi.model.rules.Rule
import ru.kima.sonar.common.serverapi.model.rules.SimpleIndicatorRule
import ru.kima.sonar.common.ui.preview.SonarPreview

@Composable
internal fun RuleView(
    rule: Rule,
    onAction: (RulesAction) -> Unit,
    modifier: Modifier = Modifier,
    depth: Int = 0,
    titleContent: @Composable (RowScope.() -> Unit)? = null
) {
    when (rule) {
        is SimpleIndicatorRule -> SimpleIndicatorRuleView(
            rule = rule,
            onAction = onAction,
            modifier = modifier,
            depth = depth,
            titleContent = titleContent
        )

        is GroupRule -> GroupRuleView(
            group = rule,
            onAction = onAction,
            modifier = modifier,
            depth = depth,
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
    verticalArrangement = Arrangement.spacedBy(16.dp)
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