package ru.kima.sonar.feature.portfolios.ui.rules.components.dialogs

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.kima.sonar.common.ui.components.SonarAlertDialog
import ru.kima.sonar.common.ui.event.LocalResultEventBus
import ru.kima.sonar.common.ui.util.CommonStrings
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.data.homeapi.model.rules.RuleType
import ru.kima.sonar.feature.portfolios.R
import ru.kima.sonar.feature.portfolios.ui.rules.events.RulesScreenBusEvent

@Composable
internal fun DeleteRuleDialog(
    key: Long,
    ruleType: RuleType,
    modifier: Modifier = Modifier
) {
    val bus = LocalResultEventBus.current
    val navigator = LocalNavigator.current
    SonarAlertDialog(
        confirmButton = {
            TextButton(
                onClick = {
                    bus.sendResult<RulesScreenBusEvent>(RulesScreenBusEvent.ConfirmDeleteRule(key))
                    navigator.goBack()
                }
            ) {
                Text(stringResource(CommonStrings.action_delete))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = { navigator.goBack() }) {
                Text(stringResource(CommonStrings.action_cancel))
            }
        },
        title = {
            val ruleName = when (ruleType) {
                RuleType.RSI -> R.string.rule_title_rsi
                RuleType.SRSI -> R.string.rule_title_srsi
                RuleType.MFI -> R.string.rule_title_mfi
                RuleType.BB -> R.string.bollinger_bands_rule_lowercase
                RuleType.GROUP -> R.string.group_rule_lowercase
            }
            Text(stringResource(R.string.delete_rule_dialog_title, stringResource(ruleName)))
        },
    )
}