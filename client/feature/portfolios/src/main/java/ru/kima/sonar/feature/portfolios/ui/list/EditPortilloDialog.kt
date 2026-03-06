package ru.kima.sonar.feature.portfolios.ui.list

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.koinViewModel
import ru.kima.sonar.common.ui.components.SonarAlertDialog
import ru.kima.sonar.common.ui.util.CommonStrings
import ru.kima.sonar.common.ui.util.clearFocusOnSoftKeyboardHide
import ru.kima.sonar.feature.portfolios.R
import ru.kima.sonar.feature.portfolios.ui.list.event.PortfolioListEvent
import ru.kima.sonar.feature.portfolios.ui.list.state.PortfolioNameDialogState

@Composable
fun EditPortilloDialog(
    modifier: Modifier = Modifier
) {
    val viewModel: PortfoliosListViewModel = koinViewModel()
    val dialogState by viewModel.dialogState.collectAsState()

    SonarAlertDialog(
        confirmButton = {
            TextButton(
                onClick = { viewModel.onEvent(PortfolioListEvent.AcceptRenamePortfolioDialog) }
            ) {
                Text(stringResource(CommonStrings.action_confirm))
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = { viewModel.onEvent(PortfolioListEvent.DismissRenamePortfolioDialog) }
            ) {
                Text(stringResource(CommonStrings.action_cancel))
            }
        },
        text = {
            OutlinedTextField(
                value = dialogState.newName,
                onValueChange = { viewModel.onEvent(PortfolioListEvent.UpdatePortfolioName(it)) },
                modifier = Modifier.clearFocusOnSoftKeyboardHide(),
                label = { Text(stringResource(R.string.label_portfolio_name)) },
                isError = dialogState.error != PortfolioNameDialogState.DialogError.NONE,
                supportingText = {
                    when (dialogState.error) {
                        PortfolioNameDialogState.DialogError.NONE -> {}
                        PortfolioNameDialogState.DialogError.BLANK_NAME -> Text(stringResource(R.string.error_blank_portfolio_name))
                    }
                }
            )
        },
    )
}