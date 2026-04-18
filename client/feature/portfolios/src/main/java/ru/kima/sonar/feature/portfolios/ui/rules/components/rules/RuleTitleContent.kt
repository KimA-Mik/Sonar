package ru.kima.sonar.feature.portfolios.ui.rules.components.rules

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import ru.kima.sonar.common.ui.util.CommonDrawables
import ru.kima.sonar.feature.portfolios.ui.rules.events.RulesAction
import ru.kima.sonar.feature.portfolios.ui.rules.model.DisplayRule

@Composable
internal fun RuleTitleContent(
    rule: DisplayRule,
    modifier: Modifier = Modifier,
    onAction: (RulesAction) -> Unit
) {
    when (rule.parent) {
        is DisplayRule.Group -> IconButton(
            onClick = { onAction(RulesAction.DeleteRule(rule.key)) },
            modifier = modifier
        ) {
            Icon(painterResource(CommonDrawables.delete_24px), contentDescription = null)
        }

        null -> {}
    }
}