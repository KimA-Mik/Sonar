package ru.kima.sonar.feature.portfolios.ui.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.kima.sonar.common.ui.components.AppBar
import ru.kima.sonar.common.ui.components.ConditionalPullToRefreshBox
import ru.kima.sonar.common.ui.components.SonarListMenu
import ru.kima.sonar.common.ui.components.SonarListMenuItem
import ru.kima.sonar.common.ui.event.ResultEffect
import ru.kima.sonar.common.ui.event.SonarEvent
import ru.kima.sonar.common.ui.navigation.Navigator
import ru.kima.sonar.common.ui.preview.SonarPreview
import ru.kima.sonar.common.ui.util.CommonStrings
import ru.kima.sonar.common.ui.util.LocalNavigator
import ru.kima.sonar.common.ui.util.LocalNumberFormat
import ru.kima.sonar.common.ui.util.LocalSnackbarHostState
import ru.kima.sonar.common.ui.util.formatBigDecimal
import ru.kima.sonar.feature.portfolios.R
import ru.kima.sonar.feature.portfolios.navigtion.PortfoliosGraph
import ru.kima.sonar.feature.portfolios.ui.addentries.event.AddEntriesResultEvent
import ru.kima.sonar.feature.portfolios.ui.details.event.PortfolioDetailsResultEvent
import ru.kima.sonar.feature.portfolios.ui.details.event.PortfolioDetailsUiEvent
import ru.kima.sonar.feature.portfolios.ui.details.event.PortfolioDetailsUserEvent
import ru.kima.sonar.feature.portfolios.ui.details.model.DisplayItemEntry
import ru.kima.sonar.feature.portfolios.ui.details.state.PortfolioDetailsState
import java.math.BigDecimal

@Composable
internal fun PortfolioDetailsScreen(
    portfolioId: Long,
    modifier: Modifier = Modifier
) {
    val viewModel: PortfolioDetailsViewModel = koinViewModel {
        parametersOf(portfolioId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val uiEvent by viewModel.uiEvents.collectAsStateWithLifecycle()

    PortfolioDetailsScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        uiEvent = uiEvent,
        modifier = modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortfolioDetailsScreenContent(
    state: PortfolioDetailsState,
    onEvent: (PortfolioDetailsUserEvent) -> Unit,
    uiEvent: SonarEvent<PortfolioDetailsUiEvent>,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val navigator = LocalNavigator.current

    LaunchedEffect(uiEvent) { consumeEvent(uiEvent, navigator) }
    ResultEffect<PortfolioDetailsResultEvent> { consumeResultEvent(it, onEvent) }
    ResultEffect<AddEntriesResultEvent> { consumeAddEntriesResultEvent(it, onEvent) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar(
                titleContent = {
                    Text(
                        text = state.name,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                navigateUp = { navigator.goBack() },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(!state.wasError) {
                FloatingActionButton(onClick = { onEvent(PortfolioDetailsUserEvent.AddEntriesButtonClicked) }) {
                    Icon(
                        painter = painterResource(ru.kima.sonar.common.ui.R.drawable.add_24px),
                        contentDescription = stringResource(R.string.content_description_add_securities)
                    )
                }
            }
        }
    ) { paddingValues ->
        PortfolioDetailsScreenBody(
            state = state,
            onEvent = onEvent,
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun PortfolioDetailsScreenBody(
    state: PortfolioDetailsState,
    onEvent: (PortfolioDetailsUserEvent) -> Unit,
    modifier: Modifier = Modifier
) = ConditionalPullToRefreshBox(
    isRefreshing = state.isLoading,
    modifier = modifier.fillMaxSize(),
    onRefresh = { onEvent(PortfolioDetailsUserEvent.Refresh) }
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            ElevatedButton(
                onClick = { onEvent(PortfolioDetailsUserEvent.OpenRulesScreenClicked) }
            ) {
                Text("Rules")
            }
        }
        val dropdownMenuItems = rememberMenuItems(onEvent)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.entries, key = { it.uid }) { entry ->
                EntryItem(
                    entry = entry,
                    dropdownMenuItems = dropdownMenuItems,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            }
        }
    }
}

@Composable
private fun rememberMenuItems(
    onEvent: (PortfolioDetailsUserEvent) -> Unit
) = remember(onEvent) {
    persistentListOf<SonarListMenuItem<String>>(
        SonarListMenuItem(
            title = CommonStrings.action_edit,
            onClick = { onEvent(PortfolioDetailsUserEvent.EditEntryButtonClicked(it)) }
        ),
        SonarListMenuItem(
            title = CommonStrings.action_delete,
            onClick = { onEvent(PortfolioDetailsUserEvent.DeleteEntryButtonClicked(it)) }
        )
    )

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EntryItem(
    entry: DisplayItemEntry,
    dropdownMenuItems: ImmutableList<SonarListMenuItem<String>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.animateContentSize(),
        shape = RoundedCornerShape(8.dp)
    ) {
        val numberFormat = LocalNumberFormat.current
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(4.dp))

                Row {
                    Text(
                        text = numberFormat.format(entry.price),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(
                            R.string.label_entry_low_price,
                            formatBigDecimal(entry.lowPrice)
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(
                            R.string.label_entry_high_price,
                            formatBigDecimal(entry.highPrice)
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (entry.note.isNotBlank()) {
                    Text(
                        text = entry.note,
                        modifier = Modifier.clickable {},
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = if (entry.showNote) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right column for optional actions / summary
            Box {
                SonarListMenu(
                    input = entry.uid,
                    items = dropdownMenuItems
                )
            }
        }
    }
}

private fun consumeEvent(
    uiEvent: SonarEvent<PortfolioDetailsUiEvent>,
    navigator: Navigator,
) {
    uiEvent.consume { event ->
        when (event) {
            is PortfolioDetailsUiEvent.OpenAddEntriesScreen ->
                navigator.navigate(PortfoliosGraph.List.Details.AddEntries(event.portfolioId))

            PortfolioDetailsUiEvent.OpenDeleteEntryDialog ->
                navigator.navigate(PortfoliosGraph.List.Details.DeleteEntryDialog)

            is PortfolioDetailsUiEvent.OpenEditEntryDialog ->
                navigator.navigate(PortfoliosGraph.List.Details.EditEntryDialog(event.entryId))

            is PortfolioDetailsUiEvent.OpenRulesScreen ->
                navigator.navigate(PortfoliosGraph.List.Details.Rules(event.portfolioId))
        }
    }
}

private fun consumeResultEvent(
    result: PortfolioDetailsResultEvent,
    onEvent: (PortfolioDetailsUserEvent) -> Unit
) {
    when (result) {
        PortfolioDetailsResultEvent.EntryUpdated -> onEvent(PortfolioDetailsUserEvent.Refresh)
    }
}

private fun consumeAddEntriesResultEvent(
    result: AddEntriesResultEvent,
    onEvent: (PortfolioDetailsUserEvent) -> Unit
) {
    when (result) {
        AddEntriesResultEvent.Success -> onEvent(PortfolioDetailsUserEvent.Refresh)
    }
}

@Preview
@Composable
private fun PortfolioDetailsContentPreview() = SonarPreview {
    PortfolioDetailsScreenContent(
        state = PortfolioDetailsState(
            name = "My Portfolio",
            entries = persistentListOf(
                DisplayItemEntry(
                    id = 1L,
                    uid = "uid-1",
                    name = "Apple Inc. (AAPL)",
                    price = BigDecimal("150.25"),
                    lowPrice = BigDecimal("145.00"),
                    highPrice = BigDecimal("155.00"),
                    note = "Bought on 2023-01-15. Long-term investment.",
                    showNote = true
                ),
                DisplayItemEntry(
                    id = 2L,
                    uid = "uid-2",
                    name = "Tesla, Inc. (TSLA)",
                    price = BigDecimal("700.50"),
                    lowPrice = BigDecimal("680.00"),
                    highPrice = BigDecimal("720.00"),
                    note = "Bought on 2023-02-10. High volatility.",
                    showNote = true
                ),
                DisplayItemEntry(
                    id = 3L,
                    uid = "uid-3",
                    name = "Amazon.com, Inc. (AMZN)",
                    price = BigDecimal("3300.75"),
                    lowPrice = BigDecimal("3200.00"),
                    highPrice = BigDecimal("3400.00"),
                    note = "Bought on 2023-03-05. Watch for earnings report.",
                    showNote = true
                ),
                DisplayItemEntry(
                    id = 4L,
                    uid = "uid-4",
                    name = "Alphabet Inc. (GOOGL)",
                    price = BigDecimal("2800.00"),
                    lowPrice = BigDecimal("2700.00"),
                    highPrice = BigDecimal("2900.00"),
                    note = "Bought on 2023-04-01. Consider selling if it hits $3000.",
                    showNote = true
                ),
                DisplayItemEntry(
                    id = 5L,
                    uid = "uid-5",
                    name = "Microsoft Corporation (MSFT)",
                    price = BigDecimal("250.00"),
                    lowPrice = BigDecimal("240.00"),
                    highPrice = BigDecimal("260.00"),
                    note = "Bought on 2023-05-20. Stable growth expected.",
                    showNote = true
                ),
                DisplayItemEntry(
                    id = 6L,
                    uid = "uid-6",
                    name = "NVIDIA Corporation (NVDA)",
                    price = BigDecimal("500.00"),
                    lowPrice = BigDecimal("480.00"),
                    highPrice = BigDecimal("520.00"),
                    note = "Bought on 2023-06-10. High risk, high reward.",
                    showNote = true
                ),
                DisplayItemEntry(
                    id = 7L,
                    uid = "uid-7",
                    name = "Meta Platforms, Inc. (META)",
                    price = BigDecimal("350.00"),
                    lowPrice = BigDecimal("330.00"),
                    highPrice = BigDecimal("370.00"),
                    note = "Bought on 2023-07-01. Monitor for changes in social media trends.",
                    showNote = true
                ),
                DisplayItemEntry(
                    id = 8L,
                    uid = "uid-8",
                    name = "Netflix, Inc. (NFLX)",
                    price = BigDecimal("600.00"),
                    lowPrice = BigDecimal("580.00"),
                    highPrice = BigDecimal("620.00"),
                    note = "Bought on 2023-08-15. Watch for subscriber growth.",
                    showNote = true
                ),
            ),
            isLoading = true,
            wasError = false
        ),
        onEvent = {},
        uiEvent = SonarEvent()
    )
}

@Preview(showBackground = true)
@Composable
private fun EntryItemPreview() = SonarPreview {
    val sample = DisplayItemEntry(
        id = 1L,
        uid = "uid-1",
        name = "Sample Security Name That Might Be Long",
        price = BigDecimal("12345.56"),
        lowPrice = BigDecimal("12000.00"),
        highPrice = BigDecimal("13000.00"),
        note = "This is a sample note for the entry. It may contain small details.",
        showNote = true
    )

    EntryItem(
        entry = sample, modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        dropdownMenuItems = persistentListOf()
    )
}