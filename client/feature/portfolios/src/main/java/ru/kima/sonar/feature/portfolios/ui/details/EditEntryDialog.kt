package ru.kima.sonar.feature.portfolios.ui.details

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.kima.sonar.common.ui.components.SonarAlertDialog
import ru.kima.sonar.common.ui.event.LocalResultEventBus
import ru.kima.sonar.common.ui.event.SonarEvent
import ru.kima.sonar.common.ui.util.CommonStrings
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.feature.portfolios.ui.components.EditEntryContent
import ru.kima.sonar.feature.portfolios.ui.details.event.EditEntryUiEvent
import ru.kima.sonar.feature.portfolios.ui.details.event.EditEntryUserEvent
import ru.kima.sonar.feature.portfolios.ui.details.event.PortfolioDetailsResultEvent
import ru.kima.sonar.feature.portfolios.ui.details.state.EditEntryDialogState

@Composable
internal fun EditEntryDialog(
    entryId: Long,
    modifier: Modifier = Modifier
) {
    val viewModel: EditEntryDialogViewModel = koinViewModel {
        parametersOf(entryId)
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
    state: EditEntryDialogState,
    uiEvent: SonarEvent<EditEntryUiEvent>,
    onEvent: (EditEntryUserEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.current
    val resultEventBus = LocalResultEventBus.current

    LaunchedEffect(uiEvent) {
        uiEvent.consume { event ->
            when (event) {
                EditEntryUiEvent.Success -> {
                    resultEventBus.sendResult<PortfolioDetailsResultEvent>(
                        PortfolioDetailsResultEvent.EntryUpdated
                    )
                    navigator.goBack()
                }
            }
        }
    }

    SonarAlertDialog(
        confirmButton = {
            TextButton(
                onClick = { onEvent(EditEntryUserEvent.ApplyChangesClicked) },
                enabled = !state.isLoading
            ) {
                Text(stringResource(CommonStrings.action_confirm))
            }
        },
        modifier = modifier.imePadding(),
        dismissButton = {
            TextButton(onClick = dropUnlessResumed { navigator.goBack() }) {
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
                if (state.isLoading) {
                    LoadingIndicator()
                } else {
                    EditEntryContent(
                        price = state.price,
                        lowPrice = state.lowPrice,
                        targetDeviation = state.targetDeviation,
                        onTargetDeviationUpdate = {
                            onEvent(EditEntryUserEvent.TargetDeviationUpdated(it))
                        },
                        onLowPriceUpdate = { onEvent(EditEntryUserEvent.LowPriceUpdated(it)) },
                        highPrice = state.highPrice,
                        onHighPriceUpdate = { onEvent(EditEntryUserEvent.HighPriceUpdated(it)) },
                        note = state.note,
                        onNoteUpdate = { onEvent(EditEntryUserEvent.NoteUpdated(it)) },
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            }
        },
        title = {
            Text(text = state.name)
        },
    )
}