package ru.kima.sonar.feature.portfolios.ui.details

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import org.koin.androidx.compose.koinViewModel
import ru.kima.sonar.common.ui.components.SonarAlertDialog
import ru.kima.sonar.common.ui.util.CommonDrawables
import ru.kima.sonar.common.ui.util.CommonStrings
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.feature.portfolios.R

@Composable
internal fun DeleteEntryDialog(modifier: Modifier = Modifier) {
    val viewModel: PortfolioDetailsViewModel = koinViewModel()
    val title by viewModel.deleteDialogTicker.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.current
    SonarAlertDialog(
        confirmButton = {
            TextButton(onClick = dropUnlessResumed {
                viewModel.acceptDeleteEntry()
                navigator.goBack()
            }) {
                Text(stringResource(CommonStrings.action_delete))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = dropUnlessResumed { navigator.goBack() }) {
                Text(stringResource(CommonStrings.action_cancel))
            }
        },
        icon = {
            Icon(
                painter = painterResource(CommonDrawables.delete_forever_24px),
                contentDescription = null
            )
        },
        title = {
            title?.let {
                Text(stringResource(R.string.dialog_title_delete_entry, it))
            }
        }
    )
}