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
import ru.kima.sonar.feature.portfolios.R
import ru.kima.sonar.feature.portfolios.ui.rules.events.RulesScreenBusEvent

@Composable
internal fun ClearGroupDialog(
    key: Long,
    modifier: Modifier = Modifier
) {
    val bus = LocalResultEventBus.current
    val navigator = LocalNavigator.current
    SonarAlertDialog(
        confirmButton = {
            TextButton(
                onClick = {
                    bus.sendResult<RulesScreenBusEvent>(RulesScreenBusEvent.ConfirmClearGroup(key))
                    navigator.goBack()
                }
            ) {
                Text(stringResource(CommonStrings.action_confirm))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = { navigator.goBack() }) {
                Text(stringResource(CommonStrings.action_cancel))
            }
        },
        title = {
            Text(stringResource(R.string.clear_group_dialog_title))
        },
    )
}