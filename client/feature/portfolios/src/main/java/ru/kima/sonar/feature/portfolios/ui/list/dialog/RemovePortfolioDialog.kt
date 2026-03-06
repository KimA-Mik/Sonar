package ru.kima.sonar.feature.portfolios.ui.list.dialog

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.kima.sonar.common.ui.components.SonarAlertDialog
import ru.kima.sonar.common.ui.event.LocalResultEventBus
import ru.kima.sonar.common.ui.event.SonarEvent
import ru.kima.sonar.common.ui.util.CommonStrings
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.feature.portfolios.R
import ru.kima.sonar.feature.portfolios.ui.list.event.PortfolioListDialogResultEvent
import ru.kima.sonar.feature.portfolios.ui.list.event.RemovePortfolioDialogUiEvent
import ru.kima.sonar.feature.portfolios.ui.list.event.RemovePortfolioDialogUserEvent
import ru.kima.sonar.feature.portfolios.ui.list.state.RemovePortfolioDialogState

@Composable
internal fun RemovePortfolioDialog(
    portfolioId: Long,
    modifier: Modifier = Modifier
) {
    val viewModel: RemovePortfolioDialogViewModel = koinViewModel {
        parametersOf(portfolioId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val uiEvent by viewModel.uiEvents.collectAsStateWithLifecycle()
    EditEntryDialogContent(
        state = state,
        uiEvent = uiEvent,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EditEntryDialogContent(
    state: RemovePortfolioDialogState,
    uiEvent: SonarEvent<RemovePortfolioDialogUiEvent>,
    onEvent: (RemovePortfolioDialogUserEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.current
    val resultEventBus = LocalResultEventBus.current

    LaunchedEffect(uiEvent) {
        uiEvent.consume { event ->
            when (event) {
                RemovePortfolioDialogUiEvent.PopBack -> navigator.goBack()
                RemovePortfolioDialogUiEvent.PopBackWithRefresh -> {
                    resultEventBus.sendResult<PortfolioListDialogResultEvent>(
                        PortfolioListDialogResultEvent.Success
                    )
                    navigator.goBack()
                }
            }
        }
    }

    SonarAlertDialog(
        confirmButton = {
            TextButton(
                onClick = { onEvent(RemovePortfolioDialogUserEvent.AcceptClicked) },
                enabled = !state.isLoading && !state.waError
            ) {
                Text(stringResource(CommonStrings.action_delete))
            }
        },
        modifier = modifier.imePadding(),
        dismissButton = {
            TextButton(onClick = { onEvent(RemovePortfolioDialogUserEvent.DismissClicked) }) {
                Text(stringResource(CommonStrings.action_cancel))
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    state.isLoading -> LoadingIndicator()
                    state.waError -> ErrorScreen(onEvent = onEvent)
                }
            }
        },
        title = {
            Text(stringResource(R.string.title_delete_portfolio, state.name))
        },
    )
}

@Composable
private fun ErrorScreen(
    onEvent: (RemovePortfolioDialogUserEvent) -> Unit,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    Text(stringResource(R.string.label_error_delete_portfolio))
    Button(onClick = { onEvent(RemovePortfolioDialogUserEvent.RefreshClicked) }) {
        Text(stringResource(CommonStrings.action_retry))
    }
}
