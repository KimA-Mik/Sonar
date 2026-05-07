package ru.kima.sonar.feature.portfolios.ui.addentries

import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.kima.sonar.common.serverapi.model.portfolio.PortfolioEntry
import ru.kima.sonar.common.serverapi.model.portfolio.StopLoss
import ru.kima.sonar.common.serverapi.model.portfolio.TakeProfit
import ru.kima.sonar.common.ui.components.AppBar
import ru.kima.sonar.common.ui.event.LocalResultEventBus
import ru.kima.sonar.common.ui.event.ResultEventBus
import ru.kima.sonar.common.ui.event.SonarEvent
import ru.kima.sonar.common.ui.navigation.Navigator
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.CommonDrawables
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.common.ui.util.LocalNumberFormat
import ru.kima.sonar.common.ui.util.LocalSnackbarHostState
import ru.kima.sonar.data.homeapi.util.getHomeApiErrorString
import ru.kima.sonar.feature.portfolios.R
import ru.kima.sonar.feature.portfolios.navigtion.PortfoliosGraph
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesResultEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesSnackbarMessage
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesUiEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesUserEvent
import ru.kima.sonar.feature.portfolios.ui.addentries.state.AddEntriesScreenState
import ru.kima.sonar.feature.portfolios.ui.components.editentry.EditEntry2Content
import ru.kima.sonar.feature.portfolios.ui.components.editentry.toComponents

@Composable
internal fun AddEntriesScreen(
    portfolioId: Long,
    modifier: Modifier = Modifier
) {
    val viewModel: AddEntriesViewModel = koinViewModel {
        parametersOf(portfolioId)
    }
    val numberFormat = LocalNumberFormat.current
    LaunchedEffect(numberFormat) {
        viewModel.setNumberFormatter(numberFormat)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val uiEvent by viewModel.uiEvents.collectAsStateWithLifecycle()

    AddEntriesScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        uiEvent = uiEvent,
        modifier = modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddEntriesScreenContent(
    state: AddEntriesScreenState,
    onEvent: (AddEntriesUserEvent) -> Unit,
    uiEvent: SonarEvent<AddEntriesUiEvent>,
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.current
    val snackbarHostState = LocalSnackbarHostState.current
    val resultEventBus = LocalResultEventBus.current
    val resources = LocalResources.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiEvent) {
        consumeUiEvent(
            uiEvent = uiEvent, navigator = navigator, resultEventBus = resultEventBus,
            resources = resources, scope = scope, snackbarHostState = snackbarHostState,
        )
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            AppBar(
                titleContent = {
                    Text(
                        text = stringResource(R.string.tittle_add_securities),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigateUp = { navigator.goBack() },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = state.components.isNotEmpty(),
                    enter = scaleIn() + slideInVertically { it / 2 },
                    exit = scaleOut() + slideOutVertically { it / 2 }
                ) {
                    SmallFloatingActionButton(
                        onClick = { onEvent(AddEntriesUserEvent.SaveChangesClicked) },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Icon(
                            painter = painterResource(CommonDrawables.save_24px),
                            contentDescription = null
                        )
                    }
                }

                FloatingActionButton(onClick = { onEvent(AddEntriesUserEvent.OpenSelectSecuritiesDialogClicked) }) {
                    Icon(
                        painterResource(CommonDrawables.add_24px),
                        contentDescription = null
                    )
                }
            }
        }
    ) { paddingValues ->
        AddEntriesScreenBody(
            state = state,
            onEvent = onEvent,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    }
}

private fun consumeUiEvent(
    uiEvent: SonarEvent<AddEntriesUiEvent>,
    navigator: Navigator,
    resultEventBus: ResultEventBus,
    resources: Resources,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    uiEvent.consume { event ->
        when (event) {
            AddEntriesUiEvent.OpenSelectSecuritiesDialog ->
                navigator.navigate(PortfoliosGraph.List.Details.AddEntries.SelectSecuritiesDialog)

            AddEntriesUiEvent.PopBackSuccess -> {
                resultEventBus.sendResult<AddEntriesResultEvent>(AddEntriesResultEvent.Success)
                navigator.goBack()
            }

            is AddEntriesUiEvent.ShowSnackbar -> {
                val message = when (event.snackbarMessage) {
                    is AddEntriesSnackbarMessage.AddedBulkSecurities ->
                        resources.getString(
                            R.string.snackbar_message_securities_recognised,
                            event.snackbarMessage.count
                        )

                    AddEntriesSnackbarMessage.NoSecuritiesFound -> resources.getString(R.string.snackbar_message_no_securities_recognised)
                    is AddEntriesSnackbarMessage.ApiError -> resources.getHomeApiErrorString(event.snackbarMessage.error)
                }

                scope.launch {
                    snackbarHostState.showSnackbar(message, withDismissAction = true)
                }
            }
        }
    }
}

@Composable
private fun AddEntriesScreenBody(
    state: AddEntriesScreenState,
    onEvent: (AddEntriesUserEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    EditEntry2Content(
        components = state.components,
        modifier = modifier,
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 80.dp),
        onDeleteEntry = { uid -> onEvent(AddEntriesUserEvent.DeleteEntry(uid)) },
        onStopLossPriceChange = { key, price ->
            onEvent(AddEntriesUserEvent.UpdateStopLossPrice(key, price))
        },
        onStopLossNoteChange = { key, note ->
            onEvent(AddEntriesUserEvent.UpdateStopLossNote(key, note))
        },
        onDeleteStopLoss = { key -> onEvent(AddEntriesUserEvent.DeleteStopLoss(key)) },
        onTakeProfitPriceChange = { key, price ->
            onEvent(AddEntriesUserEvent.UpdateTakeProfitPrice(key, price))
        },
        onTakeProfitNoteChange = { key, note ->
            onEvent(AddEntriesUserEvent.UpdateTakeProfitNote(key, note))
        },
        onDeleteTakeProfit = { key -> onEvent(AddEntriesUserEvent.DeleteTakeProfit(key)) },
        onAddStopLoss = { uid -> onEvent(AddEntriesUserEvent.AddStopLoss(uid)) },
        onAddTakeProfit = { uid -> onEvent(AddEntriesUserEvent.AddTakeProfit(uid)) }
    )
}

@Preview
@Composable
private fun AddEntriesScreenPreview() = SonarPreview {
    val components = listOf(
        PortfolioEntry(
            id = 0,
            uid = "0",
            name = "0",
            targetDeviation = 0.toBigDecimal(),
            price = 0.toBigDecimal(),
            lowPrice = 0.toBigDecimal(),
            highPrice = 0.toBigDecimal(),
            note = "Note",
            stopLosses = listOf(
                StopLoss(
                    id = 0,
                    entryId = 0,
                    price = 0.toBigDecimal(),
                    note = "Note"
                ),
            ),
            takeProfits = listOf(
                TakeProfit(
                    id = 0,
                    entryId = 0,
                    price = 0.toBigDecimal(),
                    note = "Note"
                ),
                TakeProfit(
                    id = 1,
                    entryId = 0,
                    price = 1.toBigDecimal(),
                    note = "Note"
                )
            )
        ),
        PortfolioEntry(
            id = 1,
            uid = "1",
            name = "1",
            targetDeviation = 1.toBigDecimal(),
            price = 1.toBigDecimal(),
            lowPrice = 1.toBigDecimal(),
            highPrice = 1.toBigDecimal(),
            note = "Note",
            stopLosses = emptyList(),
            takeProfits = listOf(
                TakeProfit(
                    id = 1,
                    entryId = 1,
                    price = 1.toBigDecimal(),
                    note = "Note"
                )
            )
        )
    ).toComponents()
    AddEntriesScreenContent(
        state = AddEntriesScreenState.default(
            components = components
        ),
        onEvent = {},
        uiEvent = SonarEvent()
    )
}